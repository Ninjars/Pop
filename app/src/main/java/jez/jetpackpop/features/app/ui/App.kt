package jez.jetpackpop.features.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.domain.GameChapter
import jez.jetpackpop.features.app.domain.getFirstGameConfiguration
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.app.model.app.ActiveScreen
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.ui.game.GameScreen
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.ui.AppTheme
import jez.jetpackpop.ui.overlay
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
                .background(MaterialTheme.colors.background)
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
        targetValue = if (appState.activeScreen == ActiveScreen.InGame) Color.Transparent else MaterialTheme.colors.overlay,
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value)
    ) {
        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.MainMenu,
            enter = fadeIn().plus(slideInVertically { it / 2 }),
            exit = fadeOut().plus(slideOutVertically { it }),
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
            enter = fadeIn().plus(slideInVertically { it / 2 }),
            exit = fadeOut().plus(slideOutVertically { it }),
        ) {
            VictoryMenu(
                mainMenuAction = {
                    appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
                },
            )
        }


        AnimatedVisibility(
            visible = appState.activeScreen == ActiveScreen.ChapterComplete,
            enter = fadeIn().plus(slideInVertically { it / 2 }),
            exit = fadeOut().plus(slideOutVertically { it }),
        ) {
            ChapterComplete(
                soundManager,
                appState.highScores.chapterScores,
                appState.highScores.levelScores,
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
            enter = fadeIn().plus(slideInVertically { it / 2 }),
            exit = fadeOut().plus(slideOutVertically { it }),
        ) {
            LevelEnd(
                soundManager,
                appState.hasWonActiveGame,
                appState.highScores.levelScores,
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
            when (it) {
                GameChapter.SIMPLE_SINGLE -> R.string.main_menu_chap_1
                GameChapter.SIMPLE_DECOY -> R.string.main_menu_chap_2
                GameChapter.SPLITTER -> R.string.main_menu_chap_3
            },
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
private fun ChapterComplete(
    soundManager: SoundManager,
    chapterScores: Map<GameChapter, Int>,
    levelScores: Map<GameChapter, List<HighScores.LevelScore>>,
    gameStateProvider: () -> GameState,
    nextGameAction: () -> Unit
) {
    val gameState = gameStateProvider()
    val levelScore = levelScores[gameState.config.id.chapter]
        ?.firstOrNull { it.level == gameState.config.id.id }
    GameEndMenu(
        soundManager = soundManager,
        didWin = true,
        scoreInfo = gameState.toScoreInfo(levelScore),
        startGameAction = nextGameAction,
    )
}

@Composable
private fun LevelEnd(
    soundManager: SoundManager,
    didWin: Boolean,
    levelScores: Map<GameChapter, List<HighScores.LevelScore>>,
    gameStateProvider: () -> GameState,
    nextGameAction: () -> Unit
) {
    val gameState = gameStateProvider()
    val levelScore = levelScores[gameState.config.id.chapter]
        ?.firstOrNull { it.level == gameState.config.id.id }
    GameEndMenu(
        soundManager = soundManager,
        didWin = didWin,
        scoreInfo = gameState.toScoreInfo(levelScore),
        startGameAction = nextGameAction,
    )
}

private fun GameState.toScoreInfo(
    levelScore: HighScores.LevelScore?
): ScoreInfo =
    ScoreInfo(
        remainingSeconds = remainingSeconds,
        levelScore = scoreData.gameScore,
        totalScore = scoreData.totalScore,
        levelScoreRecord = levelScore?.highestScore,
        levelTimeRecord = levelScore?.mostSecondsRemaining,
    )
