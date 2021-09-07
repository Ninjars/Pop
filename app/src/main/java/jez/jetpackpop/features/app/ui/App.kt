package jez.jetpackpop.features.app.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import jez.jetpackpop.R
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppState
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.data.GameChapter
import jez.jetpackpop.features.game.data.getFirstGameConfiguration
import jez.jetpackpop.features.game.data.getGameConfiguration
import jez.jetpackpop.features.game.data.getNextGameConfiguration
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
    gameViewModel: GameViewModel,
    viewModel: AppViewModel,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    gameEventFlow: MutableSharedFlow<GameInputEvent>,
    stateChangeListener: (AppState) -> Unit
) {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            val appState = viewModel.appState.collectAsState()
            if (appState.value is AppState.InitialisingState) {
                appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
            }

            GameScreen(
                soundManager = soundManager,
                gameState = gameViewModel.gameState.collectAsState().value,
                gameEventFlow = gameEventFlow,
            ) {
                stateChangeListener(AppState.EndMenuState(it))
            }

            when (val currentAppState = appState.value) {
                is AppState.MainMenuState -> {
                    gameEventFlow.tryEmit(GameInputEvent.StartNewGame(currentAppState.gameConfiguration))

                    ShowMainMenu(
                        gameViewModel.gameState.value.highScores,
                        stateChangeListener,
                    )
                }

                is AppState.StartGameState -> {
                    when {
                        currentAppState.isNewChapter ->
                            gameEventFlow.tryEmit(GameInputEvent.StartNextChapter(currentAppState.gameConfiguration))
                        currentAppState.isNewGame ->
                            gameEventFlow.tryEmit(GameInputEvent.StartNewGame(currentAppState.gameConfiguration))
                        else ->
                            gameEventFlow.tryEmit(GameInputEvent.StartNextLevel(currentAppState.gameConfiguration))
                    }
                    stateChangeListener(AppState.InGameState)
                }

                is AppState.InGameState -> {
                }

                is AppState.EndMenuState ->
                    EndMenu(currentAppState, appEventFlow, stateChangeListener)

                else ->
                    Log.e("App", "No ui for app state $currentAppState")
            }
        }
    }
}

@Composable
private fun ShowMainMenu(
    highScores: HighScores,
    stateChangeListener: (AppState) -> Unit,
) {
    val chapterSelectAction: (GameChapter) -> Unit = {
        stateChangeListener(
            AppState.StartGameState(
                getFirstGameConfiguration(it),
                isNewChapter = true,
                isNewGame = true,
            )
        )
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
            stateChangeListener(
                AppState.StartGameState(
                    getFirstGameConfiguration(GameChapter.SIMPLE_SINGLE),
                    isNewChapter = true,
                    isNewGame = true,
                )
            )
        },
    )
}

@Composable
private fun EndMenu(
    state: AppState.EndMenuState,
    appEventFlow: MutableSharedFlow<AppInputEvent>,
    stateChangeListener: (AppState) -> Unit,
) {
    val nextGame =
        if (state.endState.didWin) {
            getNextGameConfiguration(state.endState.gameConfigId)
        } else {
            getGameConfiguration(state.endState.gameConfigId)
        }
    if (nextGame == null) {
        VictoryMenu(
            configId = state.endState.gameConfigId,
            mainMenuAction = {
                appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
            },
            nextGameAction = null
        )

    } else {
        val isNewChapter = state.endState.gameConfigId.chapter != nextGame.id.chapter
        GameEndMenu(
            endState = state.endState,
            startGameAction = {
                stateChangeListener(
                    AppState.StartGameState(
                        nextGame,
                        isNewChapter,
                        isNewGame = false
                    )
                )
            }
        )
    }
}
