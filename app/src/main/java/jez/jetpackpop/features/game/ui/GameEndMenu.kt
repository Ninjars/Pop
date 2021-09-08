package jez.jetpackpop.features.game.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jez.jetpackpop.R
import jez.jetpackpop.ui.lose
import jez.jetpackpop.ui.onEnd
import jez.jetpackpop.ui.win

@Composable
fun GameEndMenu(
    didWin: Boolean,
    startGameAction: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (didWin) {
                DisplayMenu(
                    color = MaterialTheme.colors.win,
                    titleText = R.string.game_end_win_title,
                    buttonText = R.string.game_end_win_start,
                ) { startGameAction() }
            } else {
                DisplayMenu(
                    color = MaterialTheme.colors.lose,
                    titleText = R.string.game_end_lose_title,
                    buttonText = R.string.game_end_lose_start,
                ) { startGameAction() }
            }
        }
    }
}

@Composable
private fun DisplayMenu(
    color: Color,
    @StringRes titleText: Int,
    @StringRes buttonText: Int,
    startAction: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = color,
                shape = CircleShape
            )
            .fillMaxWidth(0.8f)
            .clip(CircleShape)
            .clickable { startAction() }
            .aspectRatio(1f, true),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = stringResource(titleText),
                style = MaterialTheme.typography.h1,
                color = MaterialTheme.colors.onEnd,
                modifier = Modifier.wrapContentSize()
            )
            Text(
                text = stringResource(buttonText),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onEnd,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}
