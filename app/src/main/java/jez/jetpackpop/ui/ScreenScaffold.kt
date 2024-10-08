package jez.jetpackpop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenScaffold(
    middleSlot: @Composable (Modifier) -> Unit,
    topSlot: @Composable (() -> Unit)? = null,
    bottomSlot: @Composable (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize()
    ) {
        topSlot?.let {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    it()
                }
            }
        }
        middleSlot(
            Modifier
                .fillMaxWidth(0.8f)
                .weight(1f)
        )
        bottomSlot?.let {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    it()
                }
            }
        }
    }
}
