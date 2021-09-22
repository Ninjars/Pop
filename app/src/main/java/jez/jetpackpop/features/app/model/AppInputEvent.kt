package jez.jetpackpop.features.app.model

import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.GameConfiguration

sealed class AppInputEvent {
    sealed class Navigation : AppInputEvent() {
        object MainMenu : Navigation()
    }

    data class StartNewGame(
        val config: GameConfiguration
    ) : AppInputEvent()

    data class StartNextChapter(
        val config: GameConfiguration
    ) : AppInputEvent()

    data class StartNextLevel(
        val config: GameConfiguration
    ) : AppInputEvent()

    data class GameEnded(val gameEndState: GameEndState) : AppInputEvent()
}
