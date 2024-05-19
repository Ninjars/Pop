package jez.jetpackpop.features.app.model.game

import jez.jetpackpop.features.app.domain.GameConfigId

sealed class GameEndState {

    abstract val gameConfigId: GameConfigId
    abstract val remainingSeconds: Int
    abstract val score: GameScoreData
    abstract val didWin: Boolean

    data class LevelEndState(
        override val gameConfigId: GameConfigId,
        override val remainingSeconds: Int,
        override val score: GameScoreData,
        override val didWin: Boolean,
    ) : GameEndState()

    data class ChapterEndState(
        override val gameConfigId: GameConfigId,
        override val remainingSeconds: Int,
        override val score: GameScoreData,
        override val didWin: Boolean,
    ) : GameEndState()
}
