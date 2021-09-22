package jez.jetpackpop.features.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameProcessState
import jez.jetpackpop.features.game.model.GameState
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
            GameProcessState.END_WIN -> {
                gameEndAction(gameState.toEndState(true))
            }
            GameProcessState.END_LOSE -> {
                gameEndAction(gameState.toEndState(false))
            }
            else -> {
            }
        }
    }

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
