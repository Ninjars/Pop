package jez.jetpackpop.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jez.jetpackpop.R
import jez.jetpackpop.ui.overlay

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.overlay)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.primaryVariant,
                        shape = CircleShape
                    )
                    .fillMaxWidth(0.8f)
                    .clip(CircleShape)
                    .clickable { startAction() }
                    .aspectRatio(1f, true),
            ) {
                Text(
                    text = stringResource(R.string.main_menu_title),
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.wrapContentSize()
                )
            }
            for (model in chapterSelectButtonModels.filter { it.highScore != null }) {
                ChapterButton(model.titleRes, model.highScore, model.chapterSelectAction)
            }
        }
    }
}

@Composable
fun ChapterButton(
    @StringRes text: Int,
    highScore: Int?,
    action: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = action)
            .clip(CircleShape)
            .background(color = MaterialTheme.colors.primary)
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .wrapContentSize()
        )
        if (highScore != null) {
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
