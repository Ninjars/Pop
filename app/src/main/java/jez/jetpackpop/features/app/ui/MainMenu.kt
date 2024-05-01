package jez.jetpackpop.features.app.ui

import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
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
import jez.jetpackpop.R
import kotlin.math.roundToInt

data class ChapterSelectButtonModel(
    @StringRes val titleRes: Int,
    val highScore: Int?,
    val chapterSelectAction: () -> Unit
)

@Composable
fun MainMenu(
    chapterSelectButtonModels: List<ChapterSelectButtonModel>,
    startAction: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize()
    ) {
        StartButton(
            startAction = startAction,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .weight(1f)
        )
        ChapterMenu(
            chapterSelectButtonModels = chapterSelectButtonModels,
        )
    }
}

@Composable
private fun StartButton(
    startAction: () -> Unit,
    modifier: Modifier = Modifier,
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
            onClick = startAction,
            shape = CircleShape,
            modifier = mod,
        ) {
            Text(
                text = stringResource(R.string.main_menu_title),
                style = MaterialTheme.typography.h1,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.wrapContentSize()
            )
        }
        Spacer(
            modifier = mod
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun ChapterMenu(
    chapterSelectButtonModels: List<ChapterSelectButtonModel>,
    modifier: Modifier = Modifier,
) {
    val visibleButtonModels = chapterSelectButtonModels.filter { it.highScore != null }
    if (visibleButtonModels.isEmpty()) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(
                color = MaterialTheme.colors.secondary,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            )
            .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        for (model in chapterSelectButtonModels.filter { it.highScore != null }) {
            ChapterButton(model.titleRes, model.highScore, model.chapterSelectAction)
        }
    }
}

@Composable
private fun ChapterButton(
    @StringRes text: Int,
    highScore: Int?,
    action: () -> Unit,
) {
    Button(
        onClick = action,
        shape = CircleShape,
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .wrapContentSize()
//                .clickable(onClick = action)
//                .clip(CircleShape)
//                .background(color = MaterialTheme.colors.primary)
//                .padding(8.dp)
        ) {
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .wrapContentSize()
            )
            if (highScore != null && highScore > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = highScore.toString(),
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(color = MaterialTheme.colors.secondary, shape = CircleShape)
                        .padding(4.dp)
                )
            }
        }
    }
}
