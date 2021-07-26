package jez.jetpackpop.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jez.jetpackpop.ui.overlay

@Composable
fun MainMenu(
    show: Boolean,
    startAction: () -> Unit
) {
    if (!show) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.overlay)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "POP",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.wrapContentSize()
            )
            Button(
                shape = CircleShape,
                onClick = {
                    Log.w("JEZTAG", "clicked start")
                    startAction()
                },
            ) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}
