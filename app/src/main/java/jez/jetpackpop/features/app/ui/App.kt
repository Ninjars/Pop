package jez.jetpackpop.features.app.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppState
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.data.GameChapter
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.game.ui.GameEndMenu
import jez.jetpackpop.features.game.ui.GameScreen
import jez.jetpackpop.features.game.ui.VictoryMenu
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.ui.AppTheme
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
@Stable
fun App(
    soundManager: SoundManager,
    appViewModel: AppViewModel,
    gameViewModel: GameViewModel,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            val appState = appViewModel.appState.collectAsState()
            val gameState = gameViewModel.gameState.collectAsState()
            if (appState.value is AppState.InitialisingState) {
                appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
            }

            GameScreen(
                soundManager = soundManager,
                gameState = gameState.value,
                gameEventFlow = gameEventFlow,
            ) {
                appEventFlow.tryEmit(AppInputEvent.GameEnded(it))
            }

            when (val currentAppState = appState.value) {
                is AppState.MainMenuState -> {
                    remember(currentAppState) {
                        gameEventFlow.tryEmit(GameInputEvent.StartNewGame(currentAppState.gameConfiguration))
                    }

                    ShowMainMenu(
                        soundManager,
                        appEventFlow,
                        gameViewModel.gameState.value.highScores,
                    )
                }

                is AppState.StartGameState -> {
                    remember(currentAppState) {
                        when {
                            currentAppState.isNewChapter ->
                                gameEventFlow.tryEmit(
                                    GameInputEvent.StartNextChapter(
                                        currentAppState.gameConfiguration
                                    )
                                )
                            currentAppState.isNewGame ->
                                gameEventFlow.tryEmit(GameInputEvent.StartNewGame(currentAppState.gameConfiguration))
                            else ->
                                gameEventFlow.tryEmit(GameInputEvent.StartNextLevel(currentAppState.gameConfiguration))
                        }
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
                    ChapterComplete(
                        soundManager,
                        appEventFlow,
                        currentAppState.nextGame
                    )

                is AppState.EndMenuState ->
                    LevelEnd(
                        soundManager,
                        appEventFlow,
                        currentAppState.didWin,
                        currentAppState.nextGameConfiguration
                    )

                else ->
                    Log.e("App", "No ui for app state $currentAppState")
            }
        }
    }
}

@Composable
private fun ShowMainMenu(
    soundManager: SoundManager,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    highScores: HighScores,
) {
    val chapterSelectAction: (GameChapter) -> Unit = {
        soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
        appEventFlow.tryEmit(AppInputEvent.StartGameFromChapter(it))
    }
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
            soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
            appEventFlow.tryEmit(AppInputEvent.StartGameFromChapter(GameChapter.SIMPLE_SINGLE))
        },
    )
}

@Composable
private fun ChapterComplete(
    soundManager: SoundManager,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    nextGame: GameConfiguration,
) {
    GameEndMenu(
        soundManager = soundManager,
        didWin = true,
        startGameAction = {
            soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
            appEventFlow.tryEmit(AppInputEvent.StartGame(nextGame, true))
        }
    )
}

@Composable
private fun LevelEnd(
    soundManager: SoundManager,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    didWin: Boolean,
    nextGameConfiguration: GameConfiguration,
) {
    GameEndMenu(
        soundManager = soundManager,
        didWin = didWin,
        startGameAction = {
            soundManager.playSound(GameSoundEffect.BUTTON_TAPPED)
            appEventFlow.tryEmit(AppInputEvent.StartGame(nextGameConfiguration, false))
        }
    )
}
