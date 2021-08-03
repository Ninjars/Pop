package jez.jetpackpop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import jez.jetpackpop.ui.overlay

@Composable
fun MainMenu(
    startAction: () -> Unit
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
                    .aspectRatio(1f, true)
            ) {
                Text(
                    text = stringResource(R.string.main_menu_title),
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.wrapContentSize()
                )
            }
            Button(
                shape = CircleShape,
                modifier = Modifier
                    .wrapContentSize(),
                onClick = {
                    startAction()
                },
            ) {
                Text(
                    text = stringResource(R.string.main_menu_start),
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}
