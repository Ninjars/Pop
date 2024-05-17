package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.math.MathUtils
import jez.jetpackpop.features.app.domain.TargetType
import jez.jetpackpop.features.app.model.game.TargetData
import jez.jetpackpop.ui.theme.GameColorsPalette
import jez.jetpackpop.ui.theme.gameColors
import android.graphics.Color as AndroidColor

@Composable
fun TargetRenderer(
    targets: List<TargetData>,
) {
    val colours = MaterialTheme.gameColors

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        targets.forEach {
            drawTargetGradient(it, colours)
        }
    }
}

@Composable
private fun DrawScope.drawTarget(data: TargetData, colours: GameColorsPalette) {
    drawCircle(
        color = data.type.toColor(colours),
        center = data.center * density,
        radius = data.radius * density,
    )
}

private fun DrawScope.drawTargetGradient(data: TargetData, colours: GameColorsPalette) {
    val radius = data.radius * density
    val center = data.center * density
    drawCircle(
        brush = Brush.radialGradient(
            *getGradientColors(data.type, colours),
            center = center - Offset(radius * 0.25f, radius * 0.25f),
            radius = radius,
        ),
        center = center,
        radius = radius,
    )
}

private fun getGradientColors(targetType: TargetType, colours: GameColorsPalette) =
    getRadialGradientColors(targetType.toColor(colours))

private val GradientColors: MutableMap<Color, Array<Pair<Float, Color>>> = mutableMapOf()
fun getRadialGradientColors(baseColor: Color) =
    GradientColors.computeIfAbsent(baseColor) {
        val gradientHSVComponents = FloatArray(3)
        AndroidColor.colorToHSV(baseColor.toArgb(), gradientHSVComponents)
        val lighter = Color.hsv(
            (gradientHSVComponents[0] * 0.99f) % 360,
            MathUtils.clamp(gradientHSVComponents[1] * 0.99f, 0f, 1f),
            MathUtils.clamp(gradientHSVComponents[2] * 1.001f, 0f, 1f),
        )
        val darker = Color.hsv(
            (gradientHSVComponents[0] * 1.02f) % 360,
            gradientHSVComponents[1],
            gradientHSVComponents[2] * 0.93f,
        )
        arrayOf(
            0f to lighter,
            0.05f to lighter,
            0.25f to baseColor,
            1f to darker,
        )
    }

private fun TargetType.toColor(colours: GameColorsPalette) =
    when (this) {
        TargetType.TARGET -> colours.target1
        TargetType.DECOY -> colours.target2
        TargetType.SPLIT_TARGET -> colours.target3
    }

@Preview(widthDp = 100, heightDp = 100)
@Composable
private fun TargetPreview() {
    TargetRenderer(
        targets = listOf(
            TargetData(
                id = "0",
                type = TargetType.TARGET,
                radius = 50f,
                center = Offset(50f, 50f),
                velocity = Offset(0f, 0f),
                clickResult = null,
            )
        )
    )
}
