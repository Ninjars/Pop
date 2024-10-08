package jez.jetpackpop.features.app.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.fastLastOrNull
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.audio.SoundManager.SoundVariance
import jez.jetpackpop.features.app.model.game.CircleEffectData
import jez.jetpackpop.features.app.model.game.CircleEffectData.EffectType
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameProcessState
import jez.jetpackpop.features.app.model.game.GameScoreData
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.model.game.TargetData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max
import kotlin.math.min

private const val EffectRadiusMiss: Float = 100f
private const val EffectDurationMsMiss: Long = 333L
private const val EffectDurationMsPop: Long = 750L

class GameLogic(
    val outputEvents: MutableSharedFlow<GameLogicEvent>,
    private val soundManager: SoundManager,
    private val width: Float,
    private val height: Float,
) {
    private val _gameState = MutableStateFlow(
        GameState(
            width = width,
            height = height,
            processState = GameProcessState.INITIALISED,
            config = GameConfiguration.Default,
            targets = emptyList(),
            effects = emptyList(),
            remainingTime = -1f,
            scoreData = createGameScore(0),
        )
    )

    val gameState: StateFlow<GameState> = _gameState

    fun processInputEvent(event: GameInputEvent) {
        with(_gameState.value) {
            _gameState.value = when (event) {
                is GameInputEvent.GameTap -> onTap(event.position)
                is GameInputEvent.StartNewGame -> startGame(
                    event.config,
                    resetScore = true,
                    scoreData = _gameState.value.scoreData,
                )

                is GameInputEvent.StartNextLevel -> continueGame(event.config, resetScore = false)
                is GameInputEvent.RestartLevel -> continueGame(event.config, resetScore = true)
                is GameInputEvent.StartNextChapter -> continueGame(event.config, resetScore = true)
                is GameInputEvent.Pause -> pause()
                is GameInputEvent.Resume -> resume()
                is GameInputEvent.SystemEvent.Paused -> pause()
                is GameInputEvent.SystemEvent.Resumed -> resume()
                is GameInputEvent.Update -> update(event.deltaSeconds)
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
        )

    private fun startGame(
        config: GameConfiguration,
        resetScore: Boolean,
        scoreData: GameScoreData,
    ): GameState {
        val targets = config.targetFactory.createTargets(
            width,
            height,
            config.targetConfigurations,
            config.interactionEnabled
        )
        return GameState(
            config = config,
            processState = GameProcessState.RUNNING,
            targets = targets,
            effects = emptyList(),
            remainingTime = config.timeLimitSeconds,
            width = width,
            height = height,
            scoreData = createGameScore(if (resetScore) 0 else scoreData.totalScore),
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

    private fun GameState.onTap(position: Offset): GameState {
        if (!config.interactionEnabled || processState != GameProcessState.RUNNING) {
            return this
        }

        val tappedTarget = targets.fastLastOrNull {
            it.clickResult != null
                    && (position - it.center).getDistanceSquared() < (it.radius * it.radius)
        }
        return if (tappedTarget == null) {
            soundManager.playEffect(GameSoundEffect.BACKGROUND_TAPPED, SoundVariance.Low)
            val now = System.currentTimeMillis()
            copy(
                scoreData = scoreData.createUpdate(false),
                effects = (effects + createMissTapEffect(
                    effectCounter,
                    position
                )).filterNot { it.endAtMs < now },
                effectCounter = effectCounter + 1,
            )
        } else {
            soundManager.playEffect(GameSoundEffect.TARGET_TAPPED, SoundVariance.High)
            onTargetTapped(tappedTarget)
        }
    }

    private fun createMissTapEffect(effectCount: Int, position: Offset): CircleEffectData {
        val now = System.currentTimeMillis()
        return CircleEffectData(
            id = effectCount,
            score = null,
            type = EffectType.MISS,
            center = position,
            startRadius = EffectRadiusMiss * 0.01f,
            endRadius = EffectRadiusMiss,
            startAtMs = now,
            endAtMs = now + EffectDurationMsMiss,
        )
    }

    private fun createPopTapEffect(
        effectCount: Int,
        position: Offset,
        radius: Float,
        targetType: TargetType,
        score: Int,
    ): CircleEffectData {
        val now = System.currentTimeMillis()
        return CircleEffectData(
            id = effectCount,
            score = score,
            type = when (targetType) {
                TargetType.DECOY,
                TargetType.TARGET -> EffectType.POP_TARGET

                TargetType.SPLIT_TARGET -> EffectType.POP_SPLIT
            },
            center = position,
            startRadius = radius,
            endRadius = radius * 3f,
            startAtMs = now,
            endAtMs = now + EffectDurationMsPop,
        )
    }

    private fun GameState.onTargetTapped(data: TargetData): GameState {
        val newTargets = when (data.clickResult) {
            null -> targets
            TargetData.ClickResult.SCORE ->
                targets.filter { it.id != data.id }
                    .toList()

            TargetData.ClickResult.SCORE_AND_SPLIT ->
                targets.filter { it.id != data.id }
                    .toMutableList()
                    .apply {
                        addAll(config.targetFactory.createSplitTargets(data, 3))
                    }
        }
        val now = System.currentTimeMillis()
        return copy(
            scoreData = scoreData.createUpdate(true),
            targets = newTargets,
            effects = (effects + createPopTapEffect(
                effectCounter,
                data.center,
                data.radius,
                data.type,
                score = max(1, scoreData.currentMultiplier),
            )).filterNot { it.endAtMs < now },
            effectCounter = effectCounter + 1,
        )
    }


    private fun GameState.update(deltaSeconds: Float) =
        if (gameIsLooping) {
            if (gameHasEnded) {
                copy(overtime = overtime + deltaSeconds)
            } else {
                config.gameLoopHandler.iterate(this, deltaSeconds, outputEvents)
            }
        }
        else this

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