package jez.jetpackpop.features.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.game.data.TargetColor
import jez.jetpackpop.features.game.model.GameScoreData
import jez.jetpackpop.features.game.model.GameState
import jez.jetpackpop.features.game.model.TargetData
import jez.jetpackpop.ui.target1
import jez.jetpackpop.ui.target2
import kotlin.math.ceil

@Composable
fun GameRenderer(
    gameState: GameState,
    targetTapListener: (TargetData) -> Unit,
) {
    gameState.targets.forEach {
        Target(it, targetTapListener)
    }
}

@Composable
fun Target(data: TargetData, onClick: (TargetData) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var modifier = Modifier
        .size(data.radius * 2f)
        .offset(data.xOffset, data.yOffset)
        .clip(CircleShape)
        .background(data.color.toColor())

    data.toOnClickAction(onClick)?.let { action ->
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
        ) { action() }
    }

    Box(
        modifier = modifier
    )
}

private fun TargetData.toOnClickAction(
    targetTapListener: (TargetData) -> Unit
): ((TargetData) -> Unit)? =
    when (this.clickResult) {
        null -> null
        TargetData.ClickResult.SCORE -> {
            { targetTapListener(this) }
        }
    }

@Composable
fun GameInfo(
    gameState: GameState
) {
    if (gameState.config.timeLimitSeconds < 0) {
        return
    }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        ScoreReadout(
            gameScoreData = gameState.scoreData,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterStart)
                .weight(1f)
        )
        Text(
            text = ceil(gameState.remainingTime).toInt().toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterEnd)
                .weight(1f)
        )
    }
}

@Composable
fun ScoreReadout(
    gameScoreData: GameScoreData,
    modifier: Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = gameScoreData.totalScore.toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = "x",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp, 0.dp)
        )
        Text(
            text = gameScoreData.currentMultiplier.toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
        )
    }
}

private fun TargetColor.toColor() =
    when (this) {
        TargetColor.TARGET -> target1
        TargetColor.DECOY -> target2
    }
