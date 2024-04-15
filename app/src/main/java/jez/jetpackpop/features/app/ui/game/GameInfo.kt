package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jez.jetpackpop.features.app.model.game.GameScoreData
import kotlin.math.ceil

@Composable
fun GameInfo(
    remainingTime: Float,
    scoreData: GameScoreData
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        ScoreReadout(
            gameScoreData = scoreData,
        )
        CountdownTimer(
            remainingSeconds = ceil(remainingTime).toInt(),
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
            modifier = Modifier
                .size(60.dp)
        )
        AnimatedCounter(
            count = remainingSeconds,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 30.sp,
            modifier = Modifier
                .align(BiasAlignment(0f, 1.8f))
        )
    }
}

@Composable
private fun ScoreReadout(
    gameScoreData: GameScoreData,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = gameScoreData.totalScore.toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = "x",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp, 0.dp)
        )
        Text(
            text = gameScoreData.currentMultiplier.toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Preview
@Composable
private fun GameInfoPreview() {
    GameInfo(
        remainingTime = 5f,
        scoreData = GameScoreData(
            startingScore = 10,
            tapHistory = emptyList(),
            gameScore = 20,
            currentMultiplier = 8
        )
    )
}
