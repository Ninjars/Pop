package jez.jetpackpop.features.app.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameProcessState
import jez.jetpackpop.features.app.model.game.GameState
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(
    soundManager: SoundManager,
    gameStateSource: State<GameState>,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    val gameState = gameStateSource.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .clipToBounds()
            .clickable(
                enabled = !gameState.config.isDemo && gameState.processState == GameProcessState.RUNNING
            ) {
                soundManager.playEffect(GameSoundEffect.BACKGROUND_TAPPED)
                gameEventFlow.tryEmit(GameInputEvent.BackgroundTap)
            }
    ) {
        TargetRenderer(
            targets = gameState.targets,
            targetTapListener = { target ->
                soundManager.playEffect(GameSoundEffect.TARGET_TAPPED)
                gameEventFlow.tryEmit(GameInputEvent.TargetTap(target))
            }
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
}

private fun GameState.shouldShowInfo() =
    remainingTime >= 0 && processState != GameProcessState.END_LOSE
