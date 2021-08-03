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
import jez.jetpackpop.model.GameEndState
import jez.jetpackpop.ui.lose
import jez.jetpackpop.ui.onEnd
import jez.jetpackpop.ui.win

@Composable
fun GameEndMenu(
    endState: GameEndState,
    startGameAction: (GameEndState) -> Unit,
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = if (endState.didWin)
                            MaterialTheme.colors.win
                        else
                            MaterialTheme.colors.lose,
                        shape = CircleShape
                    )
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f, true)
            ) {
                Text(
                    text = if (endState.didWin)
                        stringResource(R.string.game_end_lose_title)
                    else
                        stringResource(R.string.game_end_win_title),
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onEnd,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }
            Button(
                shape = CircleShape,
                onClick = {
                    startGameAction(endState)
                },
            ) {
                Text(
                    text = if (endState.didWin)
                        stringResource(R.string.game_end_win_start)
                    else
                        stringResource(R.string.game_end_lose_start),
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}