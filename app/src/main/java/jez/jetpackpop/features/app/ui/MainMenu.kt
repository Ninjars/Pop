package jez.jetpackpop.features.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
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
