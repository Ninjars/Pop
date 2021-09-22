package jez.jetpackpop.features.game.model

import jez.jetpackpop.features.game.data.GameConfiguration

sealed class GameInputEvent {
    data class StartNewGame(
        val width: Float,
        val height: Float,
        val config: GameConfiguration,
    ) : GameInputEvent()
    data class StartNextLevel(val config: GameConfiguration) : GameInputEvent()
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
