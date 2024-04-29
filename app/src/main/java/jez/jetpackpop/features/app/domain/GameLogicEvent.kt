package jez.jetpackpop.features.app.domain

import jez.jetpackpop.features.app.model.game.GameEndState

sealed class GameLogicEvent {
    data class GameEnded(
        val gameEndState: GameEndState,
    ) : GameLogicEvent()
}
