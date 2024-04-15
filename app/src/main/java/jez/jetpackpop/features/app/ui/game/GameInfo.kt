package jez.jetpackpop.features.app.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.model.game.GameScoreData
import jez.jetpackpop.features.app.model.game.GameState
import kotlin.math.ceil

@Composable
fun GameInfo(
    gameState: GameState
) {
    if (gameState.config.timeLimitSeconds < 0) {
        return
    }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        ScoreReadout(
            gameScoreData = gameState.scoreData,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterStart)
                .weight(1f)
        )
        Text(
            text = ceil(gameState.remainingTime).toInt().toString(),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterEnd)
                .weight(1f)
        )
    }
}

@Composable
private fun ScoreReadout(
    gameScoreData: GameScoreData,
    modifier: Modifier,
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