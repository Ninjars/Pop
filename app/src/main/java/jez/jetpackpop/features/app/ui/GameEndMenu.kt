package jez.jetpackpop.features.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.ui.PopMegaButton
import jez.jetpackpop.ui.lose
import jez.jetpackpop.ui.win

@Composable
fun GameEndMenu(
    soundManager: SoundManager,
    didWin: Boolean,
    startGameAction: () -> Unit,
) {
    LaunchedEffect(Unit) {
        soundManager.playSound(
            if (didWin) {
                GameSoundEffect.GAME_WIN
            } else {
                GameSoundEffect.GAME_LOSE
            }
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
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

@Composable
private fun DisplayMenu(
    color: Color,
    @StringRes titleText: Int,
    @StringRes buttonText: Int,
    onClick: () -> Unit,
) {
    PopMegaButton(
        mainText = titleText,
        subText = buttonText,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            contentColor = Color.White,
            disabledBackgroundColor = color,
            disabledContentColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth(0.8f)
    )
}
