package jez.jetpackpop.ui.components

import androidx.compose.foundation.background
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
import jez.jetpackpop.model.GameEndState
import jez.jetpackpop.model.GameProcessState
import jez.jetpackpop.model.GameState
import jez.jetpackpop.model.GameViewModel
import kotlinx.coroutines.android.awaitFrame

@Composable
fun GameScreen(
    soundManager: SoundManager,
    gameViewModel: GameViewModel,
    gameState: GameState,
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
                gameEndAction(
                    GameEndState(
                        gameState.config.id,
                        gameState.remainingTime,
                        gameState.scoreData,
                        true
                    )
                )
            }
            GameProcessState.END_LOSE -> {
                gameEndAction(
                    GameEndState(
                        gameState.config.id,
                        gameState.remainingTime,
                        gameState.scoreData,
                        false
                    )
                )
                runGameLoop(gameViewModel)
            }

            GameProcessState.RUNNING -> {
                runGameLoop(gameViewModel)
            }
        }
    }

    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .clipToBounds()
            .onSizeChanged {
                with(density) {
                    gameViewModel.onMeasured(it.width.toDp().value, it.height.toDp().value)
                }
            }
    ) {
        GameRenderer(
            showInfo = gameState.config.timeLimitSeconds.let { it >= 0 },
            gameState = gameState,
            targetTapListener = { target ->
                soundManager.playPop()
                gameViewModel.onTargetTapped(target)
            }
        )
    }
}

private suspend fun runGameLoop(
    gameViewModel: GameViewModel,
) {
    var lastFrame = 0L
    while (true) {
        val nextFrame = awaitFrame() / 1000_000L
        if (lastFrame != 0L) {
            val deltaMillis = nextFrame - lastFrame
            gameViewModel.update(deltaMillis / 1000f)
        }
        lastFrame = nextFrame
    }
}
