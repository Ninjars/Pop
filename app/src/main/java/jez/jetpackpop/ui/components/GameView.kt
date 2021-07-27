package jez.jetpackpop.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.*
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.ceil

@Composable
fun GameView(
    configuration: GameConfiguration?,
    isRunning: Boolean,
    gameEndAction: (GameEndState) -> Unit
) {
    Log.w("JEZTAG", "GameView invoked $isRunning $configuration")
    if (configuration == null) return

    val density = LocalDensity.current
    var dims by remember { mutableStateOf(Pair(0f, 0f)) }
    var gameState by rememberSaveable {
        Log.w("JEZTAG", "created gamestate $configuration")
        mutableStateOf(
            GameState(
                width = dims.first,
                height = dims.second,
                processState = GameProcessState.INSTANTIATED,
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
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
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = gameState.score.toString(),
                style = MaterialTheme.typography.h3,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart)
                    .weight(1f)
            )
            Text(
                text = ceil(gameState.remainingTime).toInt().toString(),
                style = MaterialTheme.typography.h3,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterEnd)
                    .weight(1f)
            )
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
