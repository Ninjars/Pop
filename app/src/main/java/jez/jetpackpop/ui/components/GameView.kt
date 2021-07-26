package jez.jetpackpop.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import jez.jetpackpop.model.GameConfiguration
import jez.jetpackpop.model.GameProcessState
import jez.jetpackpop.model.GameState
import jez.jetpackpop.model.TargetData
import kotlinx.coroutines.android.awaitFrame

@Composable
fun GameView(configuration: GameConfiguration, isRunning: Boolean) {
    Log.w("JEZTAG", "GameView invoked $isRunning $configuration")

    val density = LocalDensity.current
    var dims by remember { mutableStateOf(Pair(0f, 0f))}
    var gameState by rememberSaveable {
        Log.w("JEZTAG", "created gamestate $configuration")
        mutableStateOf(
            GameState(
                width = dims.first,
                height = dims.second,
                processState = GameProcessState.INSTANTIATED,
                config = configuration,
                targets = emptyList(),
            )
        )
    }

    LaunchedEffect(isRunning) {
        var lastFrame = 0L
        while (isRunning) {
            val nextFrame = awaitFrame() / 100_000L
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
                        Log.w("JEZTAG", "pendin ${gameState.processState}")
                        gameState
                    }
                }
            }
            lastFrame = nextFrame
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged {
                with(density) {
                    Log.i("JEZTAG", "onSizeChanged $it")
                    dims = Pair(it.width.toDp().value, it.height.toDp().value)
                    gameState = gameState.onMeasured(it.width.toDp().value, it.height.toDp().value)
                }
            }
        ) {
            gameState.targets.forEach {
                Target(it) { data: TargetData -> gameState = gameState.onTargetTapped(data) }
            }
        }
    }
}

@Composable
fun Target(data: TargetData, onClick: (TargetData) -> Unit) {
    Box(
        modifier = Modifier
            .size(data.radius * 2f)
            .offset(data.xOffset, data.yOffset)
            .clip(CircleShape)
            .background(data.color)
            .clickable { onClick(data) }
    )
}
