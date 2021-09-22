package jez.jetpackpop.features.app.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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

            GameScreen(
                soundManager = soundManager,
                gameState = gameState.value,
                gameEventFlow = gameEventFlow,
            )

            val highScores by rememberSaveable(gameState.value.highScores) {
                mutableStateOf(
                    gameState.value.highScores
                )
            }
            UI(
                soundManager = soundManager,
                appState = appViewModel.appState.value,
                highScores = highScores,
                appEventFlow = appEventFlow,
            )
        }
    }
}

@Composable
fun UI(
    soundManager: SoundManager,
    appState: AppState,
    highScores: HighScores,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
) {
    when (appState) {
        is AppState.MainMenuState -> {
            ShowMainMenu(
                highScores,
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
            },
            highScores.chapterScores[it]
        ) {
            chapterSelectAction(it)
        }
    }

    MainMenu(
        chapterSelectButtonModels = chapterButtonModels,
        startAction = {
            chapterSelectAction(GameChapter.SIMPLE_SINGLE)
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
