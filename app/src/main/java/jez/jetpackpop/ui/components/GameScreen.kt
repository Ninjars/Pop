package jez.jetpackpop.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import jez.jetpackpop.model.*
import kotlinx.coroutines.android.awaitFrame

@Composable
fun GameScreen(
    configuration: GameConfiguration,
    isRunning: Boolean,
    gameEndAction: (GameEndState) -> Unit
) {
    Log.w("JEZTAG", "GameView invoked $isRunning $configuration")

    var dims by remember { mutableStateOf(Pair(0f, 0f)) }
    var gameState by rememberSaveable {
        Log.w("JEZTAG", "created gamestate $configuration")
        mutableStateOf(
            GameState(
                width = dims.first,
                height = dims.second,
                processState = GameProcessState.READY,
                config = configuration,
                targets = emptyList(),
                remainingTime = -1f,
                score = 0,
            )
        )
    }

    LaunchedEffect(isRunning) {
        var lastFrame = 0L
        while (isRunning) {
            val nextFrame = awaitFrame() / 1000_000L
            if (lastFrame != 0L) {
                gameState = when (gameState.processState) {
                    GameProcessState.WAITING_MEASURE,
                    GameProcessState.READY -> gameState.start()
                    GameProcessState.RUNNING -> {
                        val period = nextFrame - lastFrame
                        gameState.update(period / 1000f)
                    }
                    GameProcessState.INSTANTIATED,
                    GameProcessState.PAUSED -> {
                        Log.w("JEZTAG", "pending ${gameState.processState}")
                        gameState
                    }
                    GameProcessState.END_WIN -> {
                        gameEndAction(
                            GameEndState(
                                gameState.remainingTime,
                                gameState.score,
                                true
                            )
                        )
                        gameState
                    }
                    GameProcessState.END_LOSE -> {
                        gameEndAction(
                            GameEndState(
                                gameState.remainingTime,
                                gameState.score,
                                false
                            )
                        )
                        gameState
                    }
                }
            }
            lastFrame = nextFrame
        }
    }

    GameRenderer(
        showInfo = true,
        gameState = gameState,
        measureListener = { width, height ->
            dims = Pair(width, height)
            gameState = gameState.onMeasured(width, height)
        },
        targetTapListener = { target -> gameState = gameState.onTargetTapped(target) }
    )
}
