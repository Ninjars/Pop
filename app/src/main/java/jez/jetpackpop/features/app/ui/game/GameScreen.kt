package jez.jetpackpop.features.app.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameProcessState
import jez.jetpackpop.features.app.model.game.GameState
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(
    gameState: GameState,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    gameEventFlow.tryEmit(GameInputEvent.GameTap(offset.div(density)))
                }
            }
    ) {
        EffectRenderer(
            effects = gameState.effects,
        )
        TargetRenderer(
            targets = gameState.targets,
        )
        AnimatedVisibility(
            visible = gameState.shouldShowInfo(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val gameInfoState = GameInfoState(
                gameState.remainingTime,
                gameState.scoreData,
            )
            GameInfo(gameInfoState)
        }
    }

    LaunchedEffect(gameState.gameIsRunning) {
        if (gameState.gameIsRunning) {
            runGameLoop(gameEventFlow)
        }
    }
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

private fun GameState.shouldShowInfo() =
    remainingTime >= 0 && processState != GameProcessState.END_LOSE
