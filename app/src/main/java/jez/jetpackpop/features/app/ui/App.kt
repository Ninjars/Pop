package jez.jetpackpop.features.app.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppState
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.data.GameChapter
import jez.jetpackpop.features.game.data.getFirstGameConfiguration
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.game.ui.GameEndMenu
import jez.jetpackpop.features.game.ui.GameScreen
import jez.jetpackpop.features.game.ui.VictoryMenu
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.ui.AppTheme
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun App(
    soundManager: SoundManager,
    appViewModel: AppViewModel,
    gameViewModel: GameViewModel,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    Log.e("App", "RECOMPOSE")
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            val gameState = gameViewModel.gameState.collectAsState()
            val appState = appViewModel.appState.collectAsState()

            GameScreen(
                soundManager = soundManager,
                gameStateSource = gameState,
                gameEventFlow = gameEventFlow,
            )

            UI(
                soundManager = soundManager,
                appStateSource = appState,
                appEventFlow = appEventFlow,
            )
        }
    }
}

@Composable
fun UI(
    soundManager: SoundManager,
    appStateSource: State<AppState>,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
) {
    val appState = appStateSource.value
    Log.e("UI", "recompose $appState")
    when (appState) {
        is AppState.MainMenuState -> {
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

        is AppState.VictoryMenuState ->
            VictoryMenu(
                mainMenuAction = {
                    appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
                },
                nextGameAction = null
            )

        is AppState.ChapterCompleteMenuState ->
            ChapterComplete(soundManager) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(
                    AppInputEvent.StartNextChapter(
                        appState.nextGameConfiguration
                    )
                )
            }

        is AppState.EndMenuState ->
            LevelEnd(
                soundManager,
                appState.didWin,
            ) {
                soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
                appEventFlow.tryEmit(
                    AppInputEvent.StartNextChapter(
                        appState.nextGameConfiguration
                    )
                )
            }

        is AppState.InGameState -> {
            // TODO: show game info here instead of within game screen?
        }
    }
}

@Composable
private fun ShowMainMenu(
    highScores: HighScores,
    chapterSelectAction: (GameChapter) -> Unit
) {
    val chapterButtonModels = GameChapter.values().map {
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
            chapterSelectAction(GameChapter.values().first())
        },
    )
}

@Composable
private fun ChapterComplete(
    soundManager: SoundManager,
    nextGameAction: () -> Unit
) {
    GameEndMenu(
        soundManager = soundManager,
        didWin = true,
        startGameAction = nextGameAction
    )
}

@Composable
private fun LevelEnd(
    soundManager: SoundManager,
    didWin: Boolean,
    nextGameAction: () -> Unit
) {
    GameEndMenu(
        soundManager = soundManager,
        didWin = didWin,
        startGameAction = nextGameAction
    )
}
