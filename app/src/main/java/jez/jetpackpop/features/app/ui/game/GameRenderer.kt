package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.domain.TargetColor
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.model.game.TargetData
import jez.jetpackpop.ui.target1
import jez.jetpackpop.ui.target2
import jez.jetpackpop.ui.target3

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
private fun Target(data: TargetData, onClick: (TargetData) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var modifier = Modifier
        .size((data.radius * 2f).dp)
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
): (() -> Unit)? =
    when (this.clickResult) {
        null -> null
        else -> {
            { targetTapListener(this) }
        }
    }

private fun TargetColor.toColor() =
    when (this) {
        TargetColor.TARGET -> target1
        TargetColor.DECOY -> target2
        TargetColor.SPLIT_TARGET -> target3
    }
