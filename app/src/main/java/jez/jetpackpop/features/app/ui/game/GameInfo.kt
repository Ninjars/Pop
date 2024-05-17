package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.model.game.GameScoreData

data class GameInfoState(
    val remainingSeconds: Int,
    val scoreData: GameScoreData,
)

@Composable
fun GameInfo(
    state: GameInfoState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Multiplier(state.scoreData.currentMultiplier)
        CountdownTimer(
            remainingSeconds = state.remainingSeconds,
        )
    }
}

@Composable
private fun CountdownTimer(
    remainingSeconds: Int,
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = GameTimer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(85.dp)
        )
        AnimatedCounter(
            count = remainingSeconds,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(BiasAlignment(0f, 0.15f))
        )
    }
}

@Composable
private fun Multiplier(
    multiplier: Int
) {
    var oldValue by remember { mutableIntStateOf(multiplier) }
    SideEffect {
        oldValue = multiplier
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(70.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            )
    ) {
        Row {
            Text(
                text = "x",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
            )
            AnimatedCounter(
                count = multiplier,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview
@Composable
private fun GameInfoPreview() {
    GameInfo(
        GameInfoState(
            remainingSeconds = 10,
            scoreData = GameScoreData(
                startingScore = 10,
                tapHistory = emptyList(),
                gameScore = 20,
                currentMultiplier = 8
            )
        )
    )
}
