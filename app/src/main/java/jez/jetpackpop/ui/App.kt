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
import jez.jetpackpop.ui.components.GameView
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

            GameView(gameConfiguration, isRunning) {
                isRunning = false
                endGameState = it
            }

            MainMenu(!isRunning && endGameState == null) {
                gameConfiguration = demoConfiguration
                isRunning = true
            }

            GameEndMenu(endGameState) {
                gameConfiguration = demoConfiguration
                endGameState = null
                isRunning = true
            }
        }
    }
}

private val demoConfiguration: GameConfiguration =
    GameConfiguration(
        randomSeed = 0,
        timeLimitSeconds = -1f,
        targetConfigurations = listOf(
            TargetConfiguration(
                color = Color.Red,
                radius = 30.dp,
                count = 10,
                minSpeed = 8.dp,
                maxSpeed = 16.dp,
            )
        )
    )
