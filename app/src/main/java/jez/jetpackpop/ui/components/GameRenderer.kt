package jez.jetpackpop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.GameState
import jez.jetpackpop.model.TargetData
import kotlin.math.ceil

@Composable
fun GameRenderer(
    showInfo: Boolean,
    gameState: GameState,
    targetTapListener: (TargetData) -> Unit,
) {
    gameState.targets.forEach {
        Target(it, targetTapListener)
    }
    if (showInfo) {
        GameInfo(gameState)
    }
}

@Composable
fun Target(data: TargetData, onClick: (TargetData) -> Unit) {
    if (data.clickable) {
        Box(
            modifier = Modifier
                .size(data.radius * 2f)
                .offset(data.xOffset, data.yOffset)
                .clip(CircleShape)
                .background(data.color)
                .clickable { onClick(data) }
        )
    } else {
        Box(
            modifier = Modifier
                .size(data.radius * 2f)
                .offset(data.xOffset, data.yOffset)
                .clip(CircleShape)
                .background(data.color)
        )
    }
}

@Composable
fun GameInfo(
    gameState: GameState
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = gameState.score.toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
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
