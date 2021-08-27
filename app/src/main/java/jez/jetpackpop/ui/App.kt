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
                stateChangeListener(mainMenuState(stateChangeListener))
            }

            when (val currentAppState = appState.value) {
                is AppState.MainMenuState ->
                    MainMenu(
                        soundManager = soundManager,
                        appState.value as AppState.MainMenuState
                    )

                is AppState.StartGameState -> {
                    ShowGame(
                        soundManager = soundManager,
                        gameConfiguration = currentAppState.gameConfiguration,
                        reset = false,
                        running = false,
                        stateChangeListener = stateChangeListener,
                    )

                    stateChangeListener(
                        AppState.InGameState(
                            currentAppState.gameConfiguration,
                            isRunning = true,
                        )
                    )
                }

                is AppState.InGameState ->
                    ShowGame(
                        soundManager = soundManager,
                        gameConfiguration = currentAppState.gameConfiguration,
                        reset = false,
                        running = currentAppState.isRunning,
                        stateChangeListener = stateChangeListener
                    )

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
    soundManager: SoundManager,
    state: AppState.MainMenuState
) {
    GameScreen(
        soundManager,
        state.gameConfiguration,
        isRunning = true,
        shouldReset = false,
        gameEndAction = { },
    )
    MainMenu(state.startAction, state.chapterSelectAction)
}

@Composable
fun ShowGame(
    soundManager: SoundManager,
    gameConfiguration: GameConfiguration,
    reset: Boolean,
    running: Boolean,
    stateChangeListener: (AppState) -> Unit,
) {
    GameScreen(
        soundManager,
        gameConfiguration,
        isRunning = running,
        shouldReset = reset,
        gameEndAction = {
            stateChangeListener(AppState.EndMenuState(it))
        },
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
                stateChangeListener(mainMenuState(stateChangeListener))
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

private fun mainMenuState(stateChangeListener: (AppState) -> Unit) = AppState.MainMenuState(
    demoConfiguration(),
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

private fun demoConfiguration(): GameConfiguration =
    GameConfiguration(
        id = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
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
