package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import jez.jetpackpop.features.app.domain.TargetColor
import jez.jetpackpop.features.app.model.game.TargetData
import jez.jetpackpop.ui.target1
import jez.jetpackpop.ui.target2
import jez.jetpackpop.ui.target3

@Composable
fun TargetRenderer(
    targets: List<TargetData>,
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        targets.forEach {
            drawTarget(it, density)
        }
    }
}

private fun DrawScope.drawTarget(data: TargetData, scale: Float) {
    drawCircle(
        color = data.color.toColor(),
        center = data.center * scale,
        radius = data.radius * scale,
    )
}

private fun TargetColor.toColor() =
    when (this) {
        TargetColor.TARGET -> target1
        TargetColor.DECOY -> target2
        TargetColor.SPLIT_TARGET -> target3
    }
