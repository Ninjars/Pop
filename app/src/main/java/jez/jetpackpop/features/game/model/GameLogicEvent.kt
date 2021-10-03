package jez.jetpackpop.features.game.model

import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.highscore.HighScores

sealed class GameLogicEvent {
    data class GameEnded(
        val highScores: HighScores,
        val config: GameConfiguration,
        val gameEndState: GameEndState,
    ) : GameLogicEvent()
}
