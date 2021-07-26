package jez.jetpackpop.ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

val Colors.overlay: Color @Composable get() = Color(0f, 0f, 0f, 0.33f)
