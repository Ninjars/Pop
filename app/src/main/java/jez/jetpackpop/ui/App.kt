package jez.jetpackpop.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.AppState
import jez.jetpackpop.model.GameConfiguration
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
            val appState = rememberSaveable { mutableStateOf<AppState>(AppState.InitialisingState) }

//            when (appState.value) {
//                AppState.InitialisingState -> {
//                    appState.value = AppState.MainMenuState(demoConfiguration(target1)) {
//                        appState.value = AppState.StartGameState(lvlConfiguration(target1))
//                    }
//                }
//                is AppState.MainMenuState -> {
//                    ShowMainMenu(appState.value)
//                }
//                is AppState.StartGameState -> {
//                    InitialiseGame(appState)
//                }
//                is AppState.InGameState -> {
//                    ShowGame(appState)
//                }
//                is AppState.EndMenuState -> {
//                    ShowEndMenu(appState)
//                }
//            }
            if (appState.value is AppState.InitialisingState) {
                appState.value = AppState.MainMenuState(demoConfiguration(target1)) {
                    appState.value = AppState.StartGameState(lvlConfiguration(target1))
                }
            }
            ShowMainMenu(appState.value)
            InitialiseGame(appState)
            ShowGame(appState)
            ShowEndMenu(appState)
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
    appState: MutableState<AppState>,
) {
    val state = appState.value
    if (state is AppState.StartGameState) {
        GameScreen(
            state.gameConfiguration,
            isRunning = false,
            shouldReset = true,
            gameEndAction = { appState.value = AppState.EndMenuState(it) },
        )

        appState.value = AppState.InGameState(
            state.gameConfiguration,
            isRunning = true,
            endGameAction = {
                appState.value = AppState.EndMenuState(it)
            }
        )
    }
}

@Composable
fun ShowGame(
    appState: MutableState<AppState>,
) {
    val state = appState.value
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
    appState: MutableState<AppState>,
) {
    val state = appState.value
    if (state is AppState.EndMenuState) {
        GameEndMenu(
            endState = state.endState,
            startGameAction = {
                appState.value = AppState.StartGameState(lvlConfiguration(target1))
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
