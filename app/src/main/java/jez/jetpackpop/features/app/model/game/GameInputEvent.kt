package jez.jetpackpop.features.app.model.game

import androidx.compose.ui.geometry.Offset
import jez.jetpackpop.features.app.domain.GameConfiguration

sealed class GameInputEvent {
    data class StartNewGame(val config: GameConfiguration) : GameInputEvent()
    data class StartNextLevel(val config: GameConfiguration) : GameInputEvent()
    data class RestartLevel(val config: GameConfiguration) : GameInputEvent()
    data class StartNextChapter(val config: GameConfiguration) : GameInputEvent()
    data class Update(val deltaSeconds: Float) : GameInputEvent()
    data object Pause : GameInputEvent()
    data object Resume : GameInputEvent()

    data class GameTap(val position: Offset) : GameInputEvent()

    sealed class SystemEvent : GameInputEvent() {
        data object Paused : SystemEvent()
        data object Resumed : SystemEvent()
    }
}
