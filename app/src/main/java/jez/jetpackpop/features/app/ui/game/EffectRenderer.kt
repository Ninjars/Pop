package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import jez.jetpackpop.ui.theme.GameColorsPalette
import jez.jetpackpop.ui.theme.gameColors
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun EffectRenderer(
    effects: List<CircleEffectData>,
) {
    val textMeasurer = rememberTextMeasurer()
    val scoreTextStyle = MaterialTheme.typography.displayLarge
    val colours = MaterialTheme.gameColors
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val now = System.currentTimeMillis()
        effects.forEach {
            drawEffect(colours, now, it, textMeasurer, scoreTextStyle)
        }
    }
}

private fun DrawScope.drawEffect(
    colours: GameColorsPalette,
    now: Long,
    data: CircleEffectData,
    textMeasurer: TextMeasurer,
    scoreTextStyle: TextStyle,
) {
    if (now > data.endAtMs) return
    val raw = max(0, (now - data.startAtMs)) / (data.endAtMs - data.startAtMs).toFloat()
    val progress = sqrt(raw)
    val radius = lerp(data.startRadius, data.endRadius, progress) * density
    val color = data.type.toColor(colours)
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

private fun EffectType.toColor(colours: GameColorsPalette) =
    when (this) {
        EffectType.MISS -> colours.miss
        EffectType.POP_TARGET -> colours.target1
        EffectType.POP_SPLIT -> colours.target3
    }

private fun lerp(start: Float, stop: Float, amount: Float): Float {
    return start + (stop - start) * amount
}
