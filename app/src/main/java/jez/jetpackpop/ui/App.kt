package jez.jetpackpop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.GameConfiguration
import jez.jetpackpop.model.GameEndState
import jez.jetpackpop.model.TargetConfiguration
import jez.jetpackpop.ui.components.GameEndMenu
import jez.jetpackpop.ui.components.GameScreen
import jez.jetpackpop.ui.components.MainMenu

@Composable
fun App() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            var gameConfiguration by rememberSaveable { mutableStateOf<GameConfiguration?>(null) }
            var isRunning by rememberSaveable { mutableStateOf(false) }
            var endGameState by rememberSaveable { mutableStateOf<GameEndState?>(null) }

            val configuration = gameConfiguration
            if (configuration != null) {
                GameScreen(configuration, isRunning) {
                    isRunning = false
                    endGameState = it
                }
            }

            if (!isRunning && endGameState == null) {
                MainMenu {
                    gameConfiguration = demoConfiguration(target1)
                    isRunning = true
                }
            }

            val endState = endGameState
            if (endState != null) {
                GameEndMenu(endState) {
                    gameConfiguration = demoConfiguration(target1)
                    endGameState = null
                    isRunning = true
                }
            }
        }
    }
}

private fun demoConfiguration(targetColor: Color): GameConfiguration =
    GameConfiguration(
        randomSeed = 0,
        timeLimitSeconds = 30f,
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
