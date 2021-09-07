package jez.jetpackpop.features.app.model

import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.GameChapter
import jez.jetpackpop.features.game.data.GameConfiguration

sealed class AppInputEvent {
    sealed class Navigation : AppInputEvent() {
        object MainMenu : Navigation()
    }

    // TODO: reduce data needed for this event and relying more on AppViewModel internal state
    data class StartGame(val config: GameConfiguration, val isNewChapter: Boolean) : AppInputEvent()
    data class StartGameFromChapter(val gameChapter: GameChapter) : AppInputEvent()
    data class GameEnded(val gameEndState: GameEndState) : AppInputEvent()
}
