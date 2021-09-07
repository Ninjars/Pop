package jez.jetpackpop.features.game.model

import jez.jetpackpop.features.game.data.GameConfiguration

sealed class GameInputEvent {
    data class Measured(val width: Float, val height: Float) : GameInputEvent()

    data class StartNewGame(val config: GameConfiguration) : GameInputEvent()
    data class StartNextLevel(val config: GameConfiguration) : GameInputEvent()
    data class StartNextChapter(val config: GameConfiguration) : GameInputEvent()
}