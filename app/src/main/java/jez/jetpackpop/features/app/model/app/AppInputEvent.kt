package jez.jetpackpop.features.app.model.app

import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.model.game.GameEndState

sealed class AppInputEvent {
    sealed class Navigation : AppInputEvent() {
        data object MainMenu : Navigation()
        data object Back : Navigation()
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

    data class RestartLevel(
        val config: GameConfiguration
    ) : AppInputEvent()

    data class GameEnded(val gameEndState: GameEndState) : AppInputEvent()
}
