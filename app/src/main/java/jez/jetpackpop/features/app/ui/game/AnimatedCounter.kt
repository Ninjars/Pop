package jez.jetpackpop.features.app.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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

/**
 * Based on https://github.com/philipplackner/AnimatedCounterCompose
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body1,
    color: Color = MaterialTheme.colors.onPrimary,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    var oldCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val countString = count.toString()
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
