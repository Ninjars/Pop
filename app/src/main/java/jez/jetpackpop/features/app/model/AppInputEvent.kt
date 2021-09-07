package jez.jetpackpop.features.app.model

import jez.jetpackpop.features.game.data.GameChapter

sealed class AppInputEvent {
    sealed class Navigation : AppInputEvent() {
        object MainMenu : Navigation()
    }

    data class StartGameFromChapter(val gameChapter: GameChapter) : AppInputEvent()
}
