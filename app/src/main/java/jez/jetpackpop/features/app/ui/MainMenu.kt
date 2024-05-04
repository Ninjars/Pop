package jez.jetpackpop.features.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jez.jetpackpop.R
import jez.jetpackpop.ui.PopMegaButton
import jez.jetpackpop.ui.ScreenScaffold

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
    val visibleButtonModels = chapterSelectButtonModels.filter { it.highScore != null }

    ScreenScaffold(
        middleSlot = {
            PopMegaButton(
                mainText = R.string.main_menu_title,
                onClick = startAction,
                modifier = it,
            )
        },
        bottomSlot = if (visibleButtonModels.isEmpty()) {
            null
        } else {
            {
                ChapterMenu(
                    chapterSelectButtonModels = visibleButtonModels,
                )
            }
        }
    )
}

@Composable
private fun ChapterMenu(
    chapterSelectButtonModels: List<ChapterSelectButtonModel>,
    modifier: Modifier = Modifier,
) {
    Box {
        val state: LazyListState = rememberLazyListState()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            state = state,
            modifier = modifier
                .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.4f).dp)
        ) {
            items(
                key = { it.titleRes },
                items = chapterSelectButtonModels
            ) { model ->
                ChapterButton(model.titleRes, model.highScore, model.chapterSelectAction)
            }
        }
        if (state.canScrollBackward) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0.0f to MaterialTheme.colors.secondary, 0.2f to Color.Transparent,
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }
        if (state.canScrollForward) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0.8f to Color.Transparent, 1f to MaterialTheme.colors.secondary,
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
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
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
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
