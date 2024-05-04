package jez.jetpackpop.ui

import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import jez.jetpackpop.ui.autoresizetext.AutoSizeText
import kotlin.math.roundToInt

@Composable
fun PopMegaButton(
    @StringRes mainText: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes subText: Int? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    clickEnabled: Boolean = true,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        label = "button glow",
        initialValue = Color.White.copy(alpha = 0.0f),
        targetValue = Color.White.copy(alpha = 0.1f),
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse,
        )
    )
    val position by infiniteTransition.animateFloat(
        label = "button offset",
        initialValue = 8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse,
        )
    )
    val mod = Modifier
        .aspectRatio(1f, true)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.offset { IntOffset(0, position.roundToInt()) }
    ) {
        Button(
            onClick = if (clickEnabled) onClick else {
                {}
            },
            shape = CircleShape,
            colors = colors,
            modifier = mod,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            ) {
                AutoSizeText(
                    text = stringResource(mainText),
                    style = MaterialTheme.typography.h1,
                    color = colors.contentColor(enabled = true).value,
                    maxLines = 1,
                    modifier = Modifier.wrapContentSize()
                )
                subText?.let {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.h5,
                        color = colors.contentColor(enabled = true).value,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
        Spacer(
            modifier = mod
                .clip(CircleShape)
                .background(color)
        )
    }
}