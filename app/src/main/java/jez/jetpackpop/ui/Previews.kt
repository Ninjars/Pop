package jez.jetpackpop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jez.jetpackpop.R
import jez.jetpackpop.features.app.ui.ChapterSelectButtonModel
import jez.jetpackpop.features.app.ui.MainMenu
import jez.jetpackpop.features.game.data.GameChapter
import jez.jetpackpop.features.game.data.GameConfigId
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.model.GameScoreData
import jez.jetpackpop.features.game.ui.GameEndMenu
import jez.jetpackpop.features.game.ui.VictoryMenu


@Preview("MainMenu")
@Composable
fun PreviewMainMenu() {
    AppTheme {
        MainMenu(
            chapterSelectButtonModels = listOf(
                ChapterSelectButtonModel(R.string.main_menu_chap_1, 10) {}
            ),
            startAction = {},
        )
    }
}

@Preview("End Game Win")
@Composable
fun PreviewGameEndMenuWin() {
    AppTheme {
        GameEndMenu(
            endState = GameEndState.LevelEndState(
                gameConfigId = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
                remainingTime = 10f,
                score = GameScoreData(
                    startingScore = 22,
                    tapHistory = emptyList(),
                    gameScore = 10,
                    currentMultiplier = 4,
                ),
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
            endState = GameEndState.LevelEndState(
                gameConfigId = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
                remainingTime = 0f,
                score = GameScoreData(
                    startingScore = 22,
                    tapHistory = emptyList(),
                    gameScore = 10,
                    currentMultiplier = 4,
                ),
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
