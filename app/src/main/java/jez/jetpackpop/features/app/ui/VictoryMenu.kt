package jez.jetpackpop.features.app.ui

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jez.jetpackpop.R
import jez.jetpackpop.ui.PopMegaButton
import jez.jetpackpop.ui.ScreenScaffold

@Composable
fun VictoryMenu(
    mainMenuAction: () -> Unit,
) {
    ScreenScaffold(
        middleSlot = {
            PopMegaButton(
                mainText = R.string.victory_title,
                onClick = mainMenuAction,
                modifier = it
            )
        },
        bottomSlot = {
            Button(
                shape = CircleShape,
                modifier = Modifier
                    .wrapContentSize(),
                onClick = mainMenuAction,
            ) {
                Text(
                    text = stringResource(R.string.victory_menu),
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.wrapContentSize()
                )
            }
        },
    )
}
