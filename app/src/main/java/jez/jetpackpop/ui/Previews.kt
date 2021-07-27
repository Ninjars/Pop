package jez.jetpackpop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jez.jetpackpop.model.GameEndState
import jez.jetpackpop.ui.components.GameEndMenu
import jez.jetpackpop.ui.components.MainMenu


@Preview("MainMenu")
@Composable
fun PreviewMainMenu() {
    AppTheme {
        MainMenu() {}
    }
}

@Preview("End Game Win")
@Composable
fun PreviewGameEndMenuWin() {
    AppTheme {
        GameEndMenu(
            endState = GameEndState(
                remainingTime = 10f,
                score = 22,
                didWin = true,
            )
        ) {}
    }
}

@Preview("End Game Lose")
@Composable
fun PreviewGameEndMenuLose() {
    AppTheme {
        GameEndMenu(
            endState = GameEndState(
                remainingTime = 0f,
                score = 11,
                didWin = false,
            )
        ) {}
    }
}
