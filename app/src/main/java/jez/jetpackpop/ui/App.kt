package jez.jetpackpop.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.AppState
import jez.jetpackpop.model.GameConfiguration
import jez.jetpackpop.model.MutableAppState
import jez.jetpackpop.model.TargetConfiguration
import jez.jetpackpop.ui.components.GameEndMenu
import jez.jetpackpop.ui.components.GameScreen
import jez.jetpackpop.ui.components.MainMenu

@Composable
@Stable
fun App() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
//            var gameConfiguration by rememberSaveable { mutableStateOf<GameConfiguration?>(null) }
//            var isRunning by rememberSaveable { mutableStateOf(false) }
//            var endGameState by rememberSaveable { mutableStateOf<GameEndState?>(null) }
            val appState = remember { MutableAppState() }

            if (appState.state is AppState.InitialisingState) {
                appState.state = AppState.MainMenuState(demoConfiguration(target1)) {
                    appState.state = AppState.StartGameState(lvlConfiguration(target1))
                }
            }
            ShowMainMenu(appState.state)
            InitialiseGame(appState)
            ShowGame(appState)
            ShowEndMenu(appState)

            Log.w("JEZTAG", "recomposing app with state ${appState.state}")
//            appState.state = when (val currentState = appState.state) {
//                is AppState.InitialisingState ->
//                    AppState.MainMenuState(demoConfiguration(target1)) {
//                        appState.state = AppState.StartGameState(demoConfiguration(target1))
//                    }
//                is AppState.MainMenuState -> {
//                    ShowMainMenu(currentState)
//                    currentState
//                }
//                is AppState.StartGameState -> {
//                    ShowGame(
//                        currentState.gameConfiguration,
//                        isRunning = false,
//                        reset = true,
//                    ) {
//                        appState.state = AppState.EndMenuState(it)
//                    }
//                    AppState.InGameState(
//                        currentState.gameConfiguration,
//                        isRunning = true,
//                    ) {
//                        appState.state = AppState.EndMenuState(it)
//                    }
//                }
//                is AppState.InGameState -> {
//                    ShowGame(
//                        currentState.gameConfiguration,
//                        isRunning = currentState.isRunning,
//                        reset = false,
//                        endGameAction = currentState.endGameAction,
//                    )
//                    currentState
//                }
//                is AppState.EndMenuState -> {
//                    ShowEndMenu(currentState) {
//                        appState.state = AppState.StartGameState(demoConfiguration(target1))
//                    }
//                    currentState
//                }
//            }
        }
    }
}

@Composable
private fun ShowMainMenu(state: AppState) {
    if (state is AppState.MainMenuState) {
        GameScreen(
            state.gameConfiguration,
            isRunning = true,
            shouldReset = false,
            gameEndAction = { },
        )
        MainMenu(state.startAction)
    }
}

@Composable
fun InitialiseGame(
    appState: MutableAppState,
) {
    val state = appState.state
    if (state is AppState.StartGameState) {
        GameScreen(
            state.gameConfiguration,
            isRunning = false,
            shouldReset = true,
            gameEndAction = { appState.state = AppState.EndMenuState(it) },
        )

        appState.state = AppState.InGameState(
            state.gameConfiguration,
            isRunning = true,
            endGameAction = {
                appState.state = AppState.EndMenuState(it)
            }
        )
    }
}

@Composable
fun ShowGame(
    appState: MutableAppState,
) {
    val state = appState.state
    if (state is AppState.InGameState) {
        GameScreen(
            state.gameConfiguration,
            isRunning = state.isRunning,
            shouldReset = false,
            gameEndAction = state.endGameAction,
        )
    }
}

@Composable
private fun ShowEndMenu(
    appState: MutableAppState,
) {
    val state = appState.state
    if (state is AppState.EndMenuState) {
        GameEndMenu(
            endState = state.endState,
            startGameAction = {
                appState.state = AppState.StartGameState(lvlConfiguration(target1))
            }
        )
    }
}

private fun demoConfiguration(targetColor: Color): GameConfiguration =
    GameConfiguration(
        randomSeed = 0,
        timeLimitSeconds = -1f,
        targetConfigurations = listOf(
            TargetConfiguration(
                color = targetColor,
                radius = 30.dp,
                count = 10,
                minSpeed = 8.dp,
                maxSpeed = 16.dp,
            )
        )
    )

private fun lvlConfiguration(targetColor: Color): GameConfiguration =
    GameConfiguration(
        randomSeed = 0,
        timeLimitSeconds = 30f,
        targetConfigurations = listOf(
            TargetConfiguration(
                color = targetColor,
                radius = 20.dp,
                count = 20,
                minSpeed = 32.dp,
                maxSpeed = 64.dp,
            )
        )
    )
