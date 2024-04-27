package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import jez.jetpackpop.features.app.domain.TargetType
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
            drawTarget(it)
        }
    }
}

private fun DrawScope.drawTarget(data: TargetData) {
    drawCircle(
        color = data.type.toColor(),
        center = data.center * density,
        radius = data.radius * density,
    )
}

private fun TargetType.toColor() =
    when (this) {
        TargetType.TARGET -> target1
        TargetType.DECOY -> target2
        TargetType.SPLIT_TARGET -> target3
    }
