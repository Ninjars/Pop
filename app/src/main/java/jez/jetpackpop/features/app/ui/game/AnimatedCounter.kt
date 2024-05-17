package jez.jetpackpop.features.app.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

val stiffSpring: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessHigh)
val softSpring: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessLow)

/**
 * Based on https://github.com/philipplackner/AnimatedCounterCompose
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    fontSize: TextUnit = TextUnit.Unspecified,
    animationSpec: AnimationSpec<Float> = stiffSpring,
) {
    val animCount by animateFloatAsState(
        targetValue = count.toFloat(),
        animationSpec = animationSpec,
        label = "count anim",
    )
    var oldCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        oldCount = animCount.roundToInt()
    }
    Row(modifier = modifier) {
        val countString = animCount.roundToInt().toString()
        val oldCountString = oldCount.toString()
        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val displayChar = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                label = "AnimatedCounter $i",
                targetState = displayChar,
                transitionSpec = {
                    val factor = if (count > oldCount) 1 else -1
                    fadeIn().plus(slideInVertically { factor * it / 4 }) togetherWith fadeOut().plus(
                        slideOutVertically { factor * -it / 4 })
                }
            ) { char ->
                Text(
                    text = char.toString(),
                    style = style,
                    color = color,
                    fontSize = fontSize,
                    softWrap = false
                )
            }
        }
    }
}
