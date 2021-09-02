package jez.jetpackpop.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.model.*
import jez.jetpackpop.ui.components.GameEndMenu
import jez.jetpackpop.ui.components.GameScreen
import jez.jetpackpop.ui.components.MainMenu
import jez.jetpackpop.ui.components.VictoryMenu

@Composable
@Stable
fun App(
    soundManager: SoundManager,
    gameViewModel: GameViewModel,
    viewModel: PopViewModel,
    stateChangeListener: (AppState) -> Unit
) {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            val appState = viewModel.appState.collectAsState()
            if (appState.value is AppState.InitialisingState) {
                stateChangeListener(mainMenuState())
            }

            GameScreen(
                soundManager = soundManager,
                gameViewModel = gameViewModel,
                gameState = gameViewModel.gameState.collectAsState().value,
            ) {
                stateChangeListener(AppState.EndMenuState(it))
            }

            when (val currentAppState = appState.value) {
                is AppState.MainMenuState -> {
                    gameViewModel.start(demoConfiguration())

                    MainMenu(
                        stateChangeListener,
                    )
                }

                is AppState.StartGameState -> {
                    gameViewModel.clear()
                    gameViewModel.start(currentAppState.gameConfiguration)
                    stateChangeListener(AppState.InGameState)
                }

                is AppState.InGameState -> { }

                is AppState.EndMenuState ->
                    EndMenu(currentAppState, stateChangeListener)

                else ->
                    Log.e("App", "No ui for app state $currentAppState")
            }
        }
    }
}

@Composable
private fun MainMenu(
    stateChangeListener: (AppState) -> Unit,
) {
    MainMenu(
        startAction = {
            stateChangeListener(
                AppState.StartGameState(
                    getFirstGameConfiguration(GameChapter.SIMPLE_SINGLE)
                )
            )
        },
        chapterSelectAction = {
            stateChangeListener(
                AppState.StartGameState(getFirstGameConfiguration(it))
            )
        }
    )
}

@Composable
private fun EndMenu(
    state: AppState.EndMenuState,
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
                stateChangeListener(mainMenuState())
            },
            nextGameAction = null
        )

    } else {
        GameEndMenu(
            endState = state.endState,
            startGameAction = {
                stateChangeListener(AppState.StartGameState(nextGame))

            }
        )
    }
}

private fun mainMenuState() = AppState.MainMenuState(demoConfiguration())

private fun demoConfiguration(): GameConfiguration =
    GameConfiguration(
        id = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
        isDemo = true,
        timeLimitSeconds = -1f,
        targetConfigurations = listOf(
            TargetConfiguration(
                color = TargetColor.TARGET,
                radius = 30.dp,
                count = 10,
                minSpeed = 8.dp,
                maxSpeed = 16.dp,
                clickable = false,
            )
        )
    )
