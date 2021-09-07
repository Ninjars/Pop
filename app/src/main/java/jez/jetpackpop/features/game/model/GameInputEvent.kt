package jez.jetpackpop.features.game.model

sealed class GameInputEvent {
    data class Measured(val width: Float, val height: Float) : GameInputEvent()

}