package jez.jetpackpop.features.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.domain.GameChapter
import jez.jetpackpop.features.app.domain.GameConfigId
import jez.jetpackpop.features.app.domain.gameConfigurations
import jez.jetpackpop.features.app.domain.getFirstGameConfiguration
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.app.model.app.ActiveScreen
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.ui.game.GameScreen
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.ui.theme.AppTheme
import jez.jetpackpop.ui.theme.gameColors
import jez.jetpackpop.ui.toTitleRes
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun App(
    soundManager: SoundManager,
    appViewModel: AppViewModel,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            val gameState = appViewModel.gameState.collectAsState()
            val appState = appViewModel.appState.collectAsState()

            GameScreen(
                gameState = gameState.value,
                gameEventFlow = gameEventFlow,
            )

            UI(
                soundManager = soundManager,
                appState = appState.value,
                gameStateProvider = { gameState.value },
                appEventFlow = appEventFlow,
            )
        }
    }
}

@Composable
fun UI(
    soundManager: SoundManager,
    appState: AppState,
    gameStateProvider: () -> GameState,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
) {
    val backgroundColor = animateColorAsState(
        label = "background color",
        targetValue = if (appState.activeScreen == ActiveScreen.InGame) Color.Transparent else MaterialTheme.gameColors.overlay,
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value)
    ) {
        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.MainMenu,
            enter = fadeIn().plus(scaleIn(initialScale = 2f)),
            exit = fadeOut().plus(scaleOut(targetScale = 2f)),
        ) {
            ShowMainMenu(
                appState.highScores,
            ) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(
                    AppInputEvent.StartNewGame(
                        getFirstGameConfiguration(it)
                    )
                )
            }
        }

        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.Victory,
            enter = fadeIn().plus(scaleIn(initialScale = 2f)),
            exit = fadeOut().plus(scaleOut(targetScale = 2f)),
        ) {
            VictoryMenu(
                soundManager,
                appState.highScores,
                gameStateProvider,
            ) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
            }
        }


        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.ChapterComplete,
            enter = fadeIn().plus(scaleIn(initialScale = 2f)),
            exit = fadeOut().plus(scaleOut(targetScale = 2f)),
        ) {
            ChapterComplete(
                soundManager,
                appState.highScores,
                gameStateProvider,
            ) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(
                    AppInputEvent.StartNextChapter(
                        appState.nextGameConfiguration
                    )
                )
            }
        }


        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.GameEnd,
            enter = fadeIn().plus(scaleIn(initialScale = 2f)),
            exit = fadeOut().plus(scaleOut(targetScale = 2f)),
        ) {
            LevelEnd(
                soundManager,
                appState.hasWonActiveGame,
                appState.highScores,
                gameStateProvider,
            ) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(
                    if (appState.hasWonActiveGame) {
                        AppInputEvent.StartNextLevel(
                            appState.nextGameConfiguration
                        )
                    } else {
                        AppInputEvent.RestartLevel(
                            appState.nextGameConfiguration
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ShowMainMenu(
    highScores: HighScores,
    chapterSelectAction: (GameChapter) -> Unit
) {
    val chapterButtonModels = GameChapter.entries.map {
        ChapterSelectButtonModel(
            it.toTitleRes(),
            highScores.chapterScores[it]
        ) {
            chapterSelectAction(it)
        }
    }

    MainMenu(
        chapterSelectButtonModels = chapterButtonModels,
        startAction = {
            chapterSelectAction(GameChapter.entries.first())
        },
    )
}

@Composable
fun VictoryMenu(
    soundManager: SoundManager,
    highScores: HighScores,
    gameStateProvider: () -> GameState,
    mainMenuAction: () -> Unit,
) {
    val gameState = gameStateProvider()
    val levelId = gameState.config.id
    val levelScore = highScores.levelScores[levelId.chapter]
        ?.firstOrNull { it.level == levelId.id }
    val chapterScore = highScores.chapterScores[levelId.chapter]
    GameWinMenu(
        soundManager = soundManager,
        scoreInfo = gameState.toScoreInfo(levelScore, chapterScore),
        levelInfo = levelId.toLevelInfo(),
        onClick = mainMenuAction,
        titleText = R.string.victory_title,
        ctaText = R.string.victory_menu,
    )
}

@Composable
private fun ChapterComplete(
    soundManager: SoundManager,
    highScores: HighScores,
    gameStateProvider: () -> GameState,
    nextGameAction: () -> Unit
) {
    val gameState = gameStateProvider()
    val levelId = gameState.config.id
    val levelScore = highScores.levelScores[levelId.chapter]
        ?.firstOrNull { it.level == levelId.id }
    val chapterScore = highScores.chapterScores[levelId.chapter]
    GameWinMenu(
        soundManager = soundManager,
        scoreInfo = gameState.toScoreInfo(levelScore, chapterScore),
        levelInfo = levelId.toLevelInfo(),
        onClick = nextGameAction,
    )
}

@Composable
private fun LevelEnd(
    soundManager: SoundManager,
    didWin: Boolean,
    highScores: HighScores,
    gameStateProvider: () -> GameState,
    nextGameAction: () -> Unit
) {
    val gameState = gameStateProvider()
    val levelId = gameState.config.id
    val levelScore = highScores.levelScores[levelId.chapter]
        ?.firstOrNull { it.level == levelId.id }
    val chapterScore = highScores.chapterScores[levelId.chapter]
    if (didWin) {
        GameWinMenu(
            soundManager = soundManager,
            scoreInfo = gameState.toScoreInfo(levelScore, chapterScore),
            levelInfo = levelId.toLevelInfo(),
            onClick = nextGameAction,
        )
    } else {
        GameLoseMenu(
            soundManager = soundManager,
            scoreInfo = gameState.toScoreInfo(levelScore, chapterScore),
            levelInfo = levelId.toLevelInfo(),
            onClick = nextGameAction,
        )
    }
}

private fun GameState.toScoreInfo(
    levelScore: HighScores.LevelScore?,
    chapterScore: Int?,
): ScoreInfo =
    ScoreInfo(
        remainingSeconds = remainingSeconds,
        levelScore = scoreData.gameScore,
        totalScore = scoreData.totalScore,
        levelScoreRecord = levelScore?.highestScore,
        levelTimeRecord = levelScore?.mostSecondsRemaining,
        chapterScoreRecord = chapterScore,
    )

@Composable
private fun GameConfigId.toLevelInfo() =
    LevelInfo(
        chapterName = stringResource(chapter.toTitleRes()),
        totalLevels = gameConfigurations[chapter]?.size ?: 0,
        currentLevel = id,
    )
