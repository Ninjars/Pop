package jez.jetpackpop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class AppState : Parcelable {
    @Parcelize
    object InitialisingState : AppState()

    @Parcelize
    data class MainMenuState(
        val gameConfiguration: GameConfiguration,
    ) : AppState()

    @Parcelize
    data class StartGameState(
        val gameConfiguration: GameConfiguration,
        val isNewChapter: Boolean,
    ) : AppState()

    @Parcelize
    object InGameState : AppState()

    @Parcelize
    data class EndMenuState(
        val endState: GameEndState,
    ) : AppState()
}
