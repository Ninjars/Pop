package jez.jetpackpop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class AppState : Parcelable {
    @Parcelize
    object InitialisingState : AppState()

    @Parcelize
    data class MainMenuState(
        val gameConfiguration: GameConfiguration,
        val startAction: () -> Unit,
        val chapterSelectAction: (GameChapter) -> Unit,
    ) : AppState()

    @Parcelize
    data class StartGameState(val gameConfiguration: GameConfiguration) : AppState()

    @Parcelize
    data class InGameState(
        val gameConfiguration: GameConfiguration,
        val isRunning: Boolean,
    ) : AppState()

    @Parcelize
    data class EndMenuState(
        val endState: GameEndState,
    ) : AppState()
}
