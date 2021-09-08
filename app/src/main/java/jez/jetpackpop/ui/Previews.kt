package jez.jetpackpop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jez.jetpackpop.R
import jez.jetpackpop.audio.NoOpSoundManager
import jez.jetpackpop.features.app.ui.ChapterSelectButtonModel
import jez.jetpackpop.features.app.ui.MainMenu
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
            soundManager = NoOpSoundManager(),
            didWin = true,
        ) {}
    }
}

@Preview("End Game Lose")
@Composable
fun PreviewGameEndMenuLose() {
    AppTheme {
        GameEndMenu(
            soundManager = NoOpSoundManager(),
            didWin = false,
        ) {}
    }
}

@Preview("Victory")
@Composable
fun PreviewVictory() {
    AppTheme {
        VictoryMenu(
            mainMenuAction = { /* NA */ },
            nextGameAction = null
        )
    }
}
