package jez.jetpackpop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jez.jetpackpop.model.GameChapter
import jez.jetpackpop.model.GameConfigId
import jez.jetpackpop.model.GameEndState
import jez.jetpackpop.ui.components.GameEndMenu
import jez.jetpackpop.ui.components.MainMenu
import jez.jetpackpop.ui.components.VictoryMenu


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
                gameConfigId = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
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
                gameConfigId = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
                remainingTime = 0f,
                score = 11,
                didWin = false,
            )
        ) {}
    }
}

@Preview("Victory")
@Composable
fun PreviewVictory() {
    AppTheme {
        VictoryMenu(
            configId = GameConfigId(GameChapter.SIMPLE_SINGLE, 0),
            mainMenuAction = { /*TODO*/ },
            nextGameAction = null
        )
    }
}
