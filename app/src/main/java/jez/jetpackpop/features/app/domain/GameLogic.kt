package jez.jetpackpop.features.app.domain

import androidx.compose.ui.geometry.Offset
import jez.jetpackpop.features.app.model.game.GameEndState
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameProcessState
import jez.jetpackpop.features.app.model.game.GameScoreData
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.model.game.TargetData
import jez.jetpackpop.features.highscore.HighScores
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class GameLogic(
    val outputEvents: MutableSharedFlow<GameLogicEvent>,
    private val width: Float,
    private val height: Float,
    private val targetFactory: TargetFactory = TargetFactory(width, height)
) {
    private val _gameState = MutableStateFlow(
        GameState(
            width = width,
            height = height,
            processState = GameProcessState.INITIALISED,
            config = GameConfiguration.DEFAULT,
            targets = emptyList(),
            remainingTime = -1f,
            scoreData = createGameScore(0),
            highScores = HighScores.defaultValue
        )
    )

    val gameState: StateFlow<GameState> = _gameState

    fun processInputEvent(event: GameInputEvent) {
        with(_gameState.value) {
            _gameState.value = when (event) {
                is GameInputEvent.BackgroundTap -> onBackgroundTapped()
                is GameInputEvent.TargetTap -> onTargetTapped(event.data)
                is GameInputEvent.StartNewGame -> startGame(
                    event.config,
                    resetScore = true,
                    scoreData = _gameState.value.scoreData,
                    highScores = _gameState.value.highScores,
                )

                is GameInputEvent.StartNextLevel -> continueGame(event.config, resetScore = false)
                is GameInputEvent.StartNextChapter -> continueGame(event.config, resetScore = true)
                is GameInputEvent.Pause -> pause()
                is GameInputEvent.Resume -> resume()
                is GameInputEvent.SystemEvent.Paused -> pause()
                is GameInputEvent.SystemEvent.Resumed -> resume()
                is GameInputEvent.Update -> update(event.deltaSeconds)
                is GameInputEvent.NewHighScore -> {
                    _gameState.value.copy(highScores = event.highScores)
                }
            }
        }
    }

    private fun GameState.continueGame(
        config: GameConfiguration,
        resetScore: Boolean,
    ): GameState =
        startGame(
            config,
            resetScore,
            scoreData,
            highScores
        )

    private fun startGame(
        config: GameConfiguration,
        resetScore: Boolean,
        scoreData: GameScoreData,
        highScores: HighScores,
    ): GameState {
        val targets = targetFactory.createTargets(config.isDemo, config.targetConfigurations)
        return GameState(
            config = config,
            processState = GameProcessState.RUNNING,
            targets = targets,
            remainingTime = config.timeLimitSeconds,
            width = width,
            height = height,
            scoreData = createGameScore(if (resetScore) 0 else scoreData.totalScore),
            highScores = highScores,
        )
    }

    private fun GameState.resume() =
        when (processState) {
            GameProcessState.PAUSED ->
                copy(processState = GameProcessState.RUNNING)

            else -> this
        }

    private fun GameState.pause() =
        when (processState) {
            GameProcessState.RUNNING ->
                copy(processState = GameProcessState.PAUSED)

            else -> this
        }

    private fun GameState.onTargetTapped(data: TargetData): GameState =
        if (processState != GameProcessState.RUNNING) {
            this
        } else {
            val newTargets = when (data.clickResult) {
                null -> targets
                TargetData.ClickResult.SCORE ->
                    targets.filter { it.id != data.id || it.color != data.color }
                        .toList()

                TargetData.ClickResult.SCORE_AND_SPLIT ->
                    targets.filter { it.id != data.id || it.color != data.color }
                        .toMutableList()
                        .apply {
                            addAll(targetFactory.createSplitTargets(data, 3))
                        }
            }
            copy(
                scoreData = scoreData.createUpdate(true),
                targets = newTargets
            )
        }

    private fun GameState.onBackgroundTapped(): GameState =
        if (processState != GameProcessState.RUNNING) {
            this
        } else {
            copy(
                scoreData = scoreData.createUpdate(false),
            )
        }

    private fun GameState.update(deltaSeconds: Float) =
        when (processState) {
            GameProcessState.END_LOSE,
            GameProcessState.RUNNING -> iterateState(deltaSeconds)

            else -> this
        }

    private fun GameState.iterateState(deltaSeconds: Float): GameState {
        val nextRemainingTime =
            if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

        val isDemo = config.isDemo
        val nextProcessState = when {
            nextRemainingTime <= 0f && !isDemo -> GameProcessState.END_LOSE
            targets.none { it.clickResult != null } && !isDemo -> GameProcessState.END_WIN
            else -> processState
        }
        val processStateHasChanged = nextProcessState != processState
        return copy(
            remainingTime = nextRemainingTime,
            processState = nextProcessState,
            targets = targets.map {
                it.update(deltaSeconds, this)
            }
        ).also {
            if (processStateHasChanged) {
                when (it.processState) {
                    GameProcessState.END_WIN,
                    GameProcessState.END_LOSE -> {
                        val gameState = _gameState.value
                        outputEvents.tryEmit(
                            GameLogicEvent.GameEnded(
                                gameState.highScores,
                                gameState.config,
                                it.toEndState(),
                            )
                        )
                    }

                    else -> {
                    }
                }
            }
        }
    }

    private fun GameState.toEndState(): GameEndState =
        if (config.isLastInChapter) {
            GameEndState.ChapterEndState(
                config.id,
                remainingTime,
                scoreData,
                processState == GameProcessState.END_WIN,
                highScores.chapterScores.getOrDefault(config.id.chapter, 0),
            )
        } else {
            GameEndState.LevelEndState(
                config.id,
                remainingTime,
                scoreData,
                processState == GameProcessState.END_WIN,
            )
        }

    private fun TargetData.update(deltaTime: Float, state: GameState): TargetData {
        val projectedPosition = center + velocity * deltaTime
        var newVelocity: Offset = velocity
        if (projectedPosition.x - radius < 0) {
            newVelocity = Offset(velocity.x.absoluteValue, velocity.y)
        } else if (projectedPosition.x + radius >= state.width) {
            newVelocity = Offset(-velocity.x.absoluteValue, velocity.y)
        }
        if (projectedPosition.y - radius < 0) {
            newVelocity = Offset(velocity.x, velocity.y.absoluteValue)
        } else if (projectedPosition.y + radius >= state.height) {
            newVelocity = Offset(velocity.x, -velocity.y.absoluteValue)
        }

        val intendedPos = center + newVelocity * deltaTime
        return copy(
            center = Offset(
                x = max(radius, min(state.width - radius, intendedPos.x)),
                y = max(radius, min(state.height - radius, intendedPos.y)),
            ),
            velocity = newVelocity,
        )
    }

    private fun createGameScore(initialScore: Int) =
        GameScoreData(
            startingScore = initialScore,
            tapHistory = emptyList(),
            gameScore = 0,
            currentMultiplier = 1,
        )

    private fun GameScoreData.createUpdate(targetTapped: Boolean): GameScoreData {
        val newHistory = tapHistory + listOf(targetTapped)
        val scoreComboPair = newHistory.fold(Pair(0, 0)) { scoreComboPair, isSuccessfulTap ->
            if (isSuccessfulTap) {
                val tapScore = max(1, scoreComboPair.second * 2)
                Pair(scoreComboPair.first + tapScore, min(4, scoreComboPair.second + 1))
            } else {
                Pair(scoreComboPair.first, 0)
            }
        }
        return GameScoreData(
            startingScore = startingScore,
            tapHistory = newHistory,
            gameScore = scoreComboPair.first,
            currentMultiplier = max(1, scoreComboPair.second * 2)
        )
    }
}