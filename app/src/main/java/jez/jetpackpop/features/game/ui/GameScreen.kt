package jez.jetpackpop.features.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameProcessState
import jez.jetpackpop.features.game.model.GameState
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun GameScreen(
    soundManager: SoundManager,
    gameState: GameState,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
//    Log.w("GameScreen", "invoked $gameViewModel $gameState")
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
        GameRenderer(
            gameState = gameState,
            targetTapListener = { target ->
                soundManager.playEffect(GameSoundEffect.TARGET_TAPPED)
                gameEventFlow.tryEmit(GameInputEvent.TargetTap(target))
            }
        )
        GameInfo(gameState)
    }
}
