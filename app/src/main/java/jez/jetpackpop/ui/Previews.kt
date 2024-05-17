package jez.jetpackpop.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jez.jetpackpop.R
import jez.jetpackpop.audio.NoOpSoundManager
import jez.jetpackpop.features.app.ui.ChapterSelectButtonModel
import jez.jetpackpop.features.app.ui.GameLoseMenu
import jez.jetpackpop.features.app.ui.GameWinMenu
import jez.jetpackpop.features.app.ui.LevelInfo
import jez.jetpackpop.features.app.ui.MainMenu
import jez.jetpackpop.features.app.ui.ScoreInfo
import jez.jetpackpop.ui.theme.AppTheme


@Preview("MainMenu")
@Composable
fun PreviewMainMenu() {
    AppTheme {
        MainMenu(
            chapterSelectButtonModels = listOf(
                ChapterSelectButtonModel(R.string.chapter_title_simple_single, 10) {}
            ),
            startAction = {},
        )
    }
}

@Preview("End Game Win")
@Composable
fun PreviewGameEndMenuWin() {
    AppTheme {
        GameWinMenu(
            soundManager = NoOpSoundManager(),
            scoreInfo = ScoreInfo(
                remainingSeconds = 5,
                levelScore = 10,
                totalScore = 200,
                levelScoreRecord = 5,
                levelTimeRecord = 8,
                chapterScoreRecord = 150,
            ),
            levelInfo = LevelInfo(
                chapterName = "Chapter Name",
                totalLevels = 5,
                currentLevel = 3,
            ),
            onClick = {},
        )
    }
}

@Preview("End Game Lose")
@Composable
fun PreviewGameEndMenuLose() {
    AppTheme {
        GameLoseMenu(
            soundManager = NoOpSoundManager(),
            scoreInfo = ScoreInfo(
                remainingSeconds = 5,
                levelScore = 10,
                totalScore = 200,
                levelScoreRecord = 15,
                levelTimeRecord = 2,
                chapterScoreRecord = null,
            ),
            levelInfo = LevelInfo(
                chapterName = "Chapter Name",
                totalLevels = 5,
                currentLevel = 3,
            ),
        ) {}
    }
}
