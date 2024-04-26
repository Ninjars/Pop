package jez.jetpackpop.features.app.model.game

import jez.jetpackpop.features.app.domain.GameConfiguration

sealed class GameInputEvent {
    data class StartNewGame(val config: GameConfiguration) : GameInputEvent()
    data class StartNextLevel(val config: GameConfiguration) : GameInputEvent()
    data class RestartLevel(val config: GameConfiguration) : GameInputEvent()
    data class StartNextChapter(val config: GameConfiguration) : GameInputEvent()
    data class Update(val deltaSeconds: Float) : GameInputEvent()
    object Pause : GameInputEvent()
    object Resume : GameInputEvent()

    data class TargetTap(val data: TargetData) : GameInputEvent()
    object BackgroundTap : GameInputEvent()

    sealed class SystemEvent : GameInputEvent() {
        object Paused : SystemEvent()
        object Resumed : SystemEvent()
    }
}
