package jez.jetpackpop.features.game.model

import jez.jetpackpop.features.game.data.GameConfiguration

sealed class GameInputEvent {
    data class Measured(val width: Float, val height: Float) : GameInputEvent()

    data class StartNewGame(val config: GameConfiguration) : GameInputEvent()
    data class StartNextLevel(val config: GameConfiguration) : GameInputEvent()
    data class StartNextChapter(val config: GameConfiguration) : GameInputEvent()
    data class Update(val deltaSeconds: Float) : GameInputEvent()
    object Pause : GameInputEvent()
    object Resume : GameInputEvent()

    sealed class Interaction : GameInputEvent() {
        data class TargetTap(val data: TargetData): Interaction()
        object BackgroundTap : Interaction()
    }

    sealed class SystemEvent : GameInputEvent() {
        object Paused : SystemEvent()
        object Resumed : SystemEvent()
    }
}