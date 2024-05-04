package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.DstIn
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import jez.jetpackpop.features.app.model.game.CircleEffectData
import jez.jetpackpop.features.app.model.game.CircleEffectData.EffectType
import jez.jetpackpop.ui.missEffectColor
import jez.jetpackpop.ui.target1
import jez.jetpackpop.ui.target3
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun EffectRenderer(
    effects: List<CircleEffectData>,
) {
    val textMeasurer = rememberTextMeasurer()
    val scoreTextStyle = MaterialTheme.typography.h2
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val now = System.currentTimeMillis()
        effects.forEach {
            drawEffect(now, it, textMeasurer, scoreTextStyle)
        }
    }
}

private fun DrawScope.drawEffect(
    now: Long,
    data: CircleEffectData,
    textMeasurer: TextMeasurer,
    scoreTextStyle: TextStyle,
) {
    if (now > data.endAtMs) return
    val raw = max(0, (now - data.startAtMs)) / (data.endAtMs - data.startAtMs).toFloat()
    val progress = sqrt(raw)
    val radius = lerp(data.startRadius, data.endRadius, progress) * density
    val color = data.type.toColor()
    val center = data.center * density
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(0f to Color.Transparent, 1f to color),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius,
        colorFilter = ColorFilter.tint(
            color = Color(1f, 1f, 1f, alpha = 1f - progress),
            blendMode = DstIn,
        ),
    )

    if (data.score != null) {
        val textLayoutResult = textMeasurer.measure(
            text = "+${data.score}",
            style = scoreTextStyle,
        )
        drawText(
            textLayoutResult = textLayoutResult,
            color = color.copy(alpha = 1f - progress * progress),
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2,
            )
        )
    }
}

private fun EffectType.toColor() =
    when (this) {
        EffectType.MISS -> missEffectColor
        EffectType.POP_TARGET -> target1
        EffectType.POP_SPLIT -> target3
    }

private fun lerp(start: Float, stop: Float, amount: Float): Float {
    return start + (stop - start) * amount
}
