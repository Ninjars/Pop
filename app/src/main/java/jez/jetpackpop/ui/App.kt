package jez.jetpackpop.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
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
            LaunchedEffect(Unit) {
                if (appState.value is AppState.InitialisingState) {
                    appState.value = AppState.MainMenuState(demoConfiguration(target1)) {
                        appState.value = AppState.StartGameState(lvlConfiguration(target1))
                    }
                }
            }

            when (val currentAppState = appState.value) {
                is AppState.MainMenuState ->
                    MainMenu(appState.value as AppState.MainMenuState)

                is AppState.StartGameState ->{
                    ShowGame(
                        gameConfiguration = currentAppState.gameConfiguration,
                        reset = false,
                        running = false,
                    ) { appState.value = it }

                    appState.value = AppState.InGameState(
                        currentAppState.gameConfiguration,
                        isRunning = true,
                    )
                }

                is AppState.InGameState ->
                    ShowGame(
                        gameConfiguration = currentAppState.gameConfiguration,
                        reset = false,
                        running = currentAppState.isRunning,
                    ) { appState.value = it }

                is AppState.EndMenuState ->
                    EndMenu(currentAppState) { appState.value = it }

                else ->
                    Log.e("App", "No ui for app state $currentAppState")
            }
        }
    }
}

@Composable
private fun MainMenu(state: AppState.MainMenuState) {
    GameScreen(
        state.gameConfiguration,
        isRunning = true,
        shouldReset = false,
        gameEndAction = { },
    )
    MainMenu(state.startAction)
}

@Composable
fun ShowGame(
    gameConfiguration: GameConfiguration,
    reset: Boolean,
    running: Boolean,
    stateChangeListener: (AppState) -> Unit,
) {
    GameScreen(
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
    GameEndMenu(
        endState = state.endState,
        startGameAction = {
            stateChangeListener(AppState.StartGameState(lvlConfiguration(target1)))
        }
    )
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
                clickable = false,
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
                clickable = true,
            )
        )
    )
