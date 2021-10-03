package jez.jetpackpop.features.app.domain

import jez.jetpackpop.features.app.model.game.GameEndState
import jez.jetpackpop.features.highscore.HighScores

sealed class GameLogicEvent {
    data class GameEnded(
        val highScores: HighScores,
        val config: GameConfiguration,
        val gameEndState: GameEndState,
    ) : GameLogicEvent()
}
