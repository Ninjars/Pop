package jez.jetpackpop.features.game.model

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

class GameViewModel(
    private val highScoresRepository: HighScoresRepository,
    inputEvents: SharedFlow<GameInputEvent>,
) : ViewModel() {
    private val _gameState = MutableStateFlow(
        GameState(
            width = 0f,
            height = 0f,
            processState = GameProcessState.INITIALISED,
            config = GameConfiguration.DEFAULT,
            targets = emptyList(),
            remainingTime = -1f,
            scoreData = createGameScore(0),
            highScores = HighScores.defaultValue
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    init {
        viewModelScope.launch {
            highScoresRepository.highScoresFlow.collect {
                _gameState.value = _gameState.value.copy(highScores = it)
            }
        }

        viewModelScope.launch {
            inputEvents.collect {
                _gameState.value = processInputEvent(it)
            }
        }
    }

    private fun processInputEvent(event: GameInputEvent): GameState =
        with(gameState.value) {
            when (event) {
                is GameInputEvent.BackgroundTap -> onBackgroundTapped()
                is GameInputEvent.TargetTap -> onTargetTapped(event.data)
                is GameInputEvent.Measured -> onMeasured(event.width, event.height)
                is GameInputEvent.StartNewGame -> start(event.config, resetScore = true)
                is GameInputEvent.StartNextLevel -> start(event.config, resetScore = false)
                is GameInputEvent.StartNextChapter -> start(event.config, resetScore = true)
                is GameInputEvent.Pause -> pause()
                is GameInputEvent.Resume -> resume()
                is GameInputEvent.SystemEvent.Paused -> pause()
                is GameInputEvent.SystemEvent.Resumed -> resume()
                is GameInputEvent.Update -> update(event.deltaSeconds)
                is GameInputEvent.RecordCurrentScore -> recordCurrentScore()
            }
        }

    private fun GameState.onMeasured(width: Float, height: Float): GameState =
        when (processState) {
            GameProcessState.INITIALISED ->
                copy(
                    width = width,
                    height = height,
                    processState = GameProcessState.READY,
                )
            GameProcessState.WAITING_MEASURE ->
                copy(
                    width = width,
                    height = height,
                    processState = GameProcessState.RUNNING,
                )
            else ->
                copy(
                    width = width,
                    height = height,
                )
        }

    private fun GameState.start(
        config: GameConfiguration,
        resetScore: Boolean
    ): GameState =
        when (processState) {
            GameProcessState.WAITING_MEASURE ->
                copy(
                    config = config,
                )
            GameProcessState.INITIALISED ->
                if (width == 0f) {
                    copy(
                        config = config,
                        processState = GameProcessState.WAITING_MEASURE
                    )
                } else {
                    startGame(
                        config,
                        width,
                        height,
                        resetScore,
                    )
                }

            else ->
                startGame(
                    config,
                    width,
                    height,
                    resetScore,
                )
        }

    private fun GameState.startGame(
        config: GameConfiguration,
        width: Float,
        height: Float,
        resetScore: Boolean,
    ): GameState {
        val random = Random.Default
        val targets = config.targetConfigurations.flatMap { targetConfig ->
            (0 until targetConfig.count).map {
                TargetData(
                    id = it,
                    color = targetConfig.color,
                    radius = targetConfig.radius,
                    center = Offset(
                        random.nextFloat() * width,
                        random.nextFloat() * height
                    ),
                    velocity = getRandomVelocity(
                        random,
                        targetConfig.minSpeed.value,
                        targetConfig.maxSpeed.value
                    ),
                    clickable = targetConfig.clickable,
                )
            }
        }
        return copy(
            config = config,
            processState = GameProcessState.RUNNING,
            targets = targets,
            remainingTime = config.timeLimitSeconds,
            width = width,
            height = height,
            scoreData = createGameScore(if (resetScore) 0 else scoreData.startingScore),
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
            copy(
                scoreData = scoreData.createUpdate(true),
                targets = targets.filter { it.id != data.id || it.color != data.color }.toList()
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

    private fun GameState.recordCurrentScore(): GameState {
        viewModelScope.launch {
            highScoresRepository.updateHighScores(
                highScores.copy(
                    chapterScores = highScores.chapterScores.run {
                        val chapter = config.id.chapter
                        toMutableMap().apply {
                            this[chapter] =
                                max(getOrDefault(chapter, 0), scoreData.totalScore)
                        }
                    }
                )
            )
        }
        return this
    }

    private fun GameState.update(deltaSeconds: Float) =
        when (processState) {
            GameProcessState.INITIALISED,
            GameProcessState.READY,
            GameProcessState.WAITING_MEASURE -> start(config, resetScore = false)
            GameProcessState.END_LOSE,
            GameProcessState.RUNNING -> iterateState(deltaSeconds)
            else -> this
        }

    private fun getRandomVelocity(random: Random, min: Float, max: Float): Offset {
        val speed = random.nextFloat() * (max - min) + min
        val angle = random.nextFloat() * 2 * PI
        return Offset(
            x = (cos(angle) * speed).toFloat(),
            y = (sin(angle) * speed).toFloat(),
        )
    }

    private fun GameState.iterateState(deltaSeconds: Float): GameState {
        val nextRemainingTime =
            if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

        val isDemo = config.isDemo
        val nextProcessState = when {
            nextRemainingTime <= 0f && !isDemo -> GameProcessState.END_LOSE
            targets.none { it.clickable } && !isDemo -> GameProcessState.END_WIN
            else -> processState
        }
        return copy(
            remainingTime = nextRemainingTime,
            processState = nextProcessState,
            targets = targets.map {
                it.update(deltaSeconds, this)
            }
        )
    }

    private fun TargetData.update(deltaTime: Float, state: GameState): TargetData {
        val projectedPosition = center + velocity * deltaTime
        var newVelocity: Offset = velocity
        if (projectedPosition.x - radius.value < 0) {
            newVelocity = Offset(velocity.x.absoluteValue, velocity.y)
        } else if (projectedPosition.x + radius.value >= state.width) {
            newVelocity = Offset(-velocity.x.absoluteValue, velocity.y)
        }
        if (projectedPosition.y - radius.value < 0) {
            newVelocity = Offset(velocity.x, velocity.y.absoluteValue)
        } else if (projectedPosition.y + radius.value >= state.height) {
            newVelocity = Offset(velocity.x, -velocity.y.absoluteValue)
        }

        val intendedPos = center + newVelocity * deltaTime
        return copy(
            center = Offset(
                x = max(radius.value, min(state.width - radius.value, intendedPos.x)),
                y = max(radius.value, min(state.height - radius.value, intendedPos.y)),
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
