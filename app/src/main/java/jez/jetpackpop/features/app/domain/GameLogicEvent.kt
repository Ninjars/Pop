package jez.jetpackpop.features.app.domain

import jez.jetpackpop.features.app.model.game.GameEndState

sealed class GameLogicEvent {
    data class GameEnded(
        val config: GameConfiguration,
        val gameEndState: GameEndState,
    ) : GameLogicEvent()
}
