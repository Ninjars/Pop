package jez.jetpackpop.features.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameProcessState
import jez.jetpackpop.features.game.model.GameState
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(
    soundManager: SoundManager,
    gameState: GameState,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
    gameEndAction: (GameEndState) -> Unit,
) {
//    Log.w("GameScreen", "invoked $gameViewModel $gameState")
    LaunchedEffect(gameState.processState) {
//        Log.w("GameScreen", "$gameState.processState")
        when (gameState.processState) {
            GameProcessState.INITIALISED,
            GameProcessState.WAITING_MEASURE,
            GameProcessState.READY -> {
            }
            GameProcessState.PAUSED -> {
            }
            GameProcessState.END_WIN -> {
                gameEventFlow.tryEmit(GameInputEvent.RecordCurrentScore)
                gameEndAction(gameState.toEndState(true))
            }
            GameProcessState.END_LOSE -> {
                gameEndAction(gameState.toEndState(false))
                runGameLoop(gameEventFlow)
            }

            GameProcessState.RUNNING -> {
                runGameLoop(gameEventFlow)
            }
        }
    }

    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .clipToBounds()
            .clickable {
                gameEventFlow.tryEmit(GameInputEvent.BackgroundTap)
            }
            .onSizeChanged {
                with(density) {
                    gameEventFlow.tryEmit(
                        GameInputEvent.Measured(it.width.toDp().value, it.height.toDp().value)
                    )
                }
            }
    ) {
        GameRenderer(
            showInfo = gameState.config.timeLimitSeconds.let { it >= 0 },
            gameState = gameState,
            targetTapListener = { target ->
                soundManager.playPop()
                gameEventFlow.tryEmit(GameInputEvent.TargetTap(target))
            }
        )
    }
}

private fun GameState.toEndState(didWin: Boolean): GameEndState =
    if (config.isLastInChapter) {
        GameEndState.ChapterEndState(
            config.id,
            remainingTime,
            scoreData,
            didWin,
            highScores.chapterScores.getOrDefault(config.id.chapter, 0),
        )
    } else {
        GameEndState.LevelEndState(
            config.id,
            remainingTime,
            scoreData,
            didWin,
        )
    }

private suspend fun runGameLoop(
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    var lastFrame = 0L
    while (true) {
        val nextFrame = awaitFrame() / 1000_000L
        if (lastFrame != 0L) {
            val deltaMillis = nextFrame - lastFrame
            gameEventFlow.emit(GameInputEvent.Update(deltaMillis / 1000f))
        }
        lastFrame = nextFrame
    }
}
