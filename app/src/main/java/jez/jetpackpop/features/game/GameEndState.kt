package jez.jetpackpop.features.game

import android.os.Parcelable
import jez.jetpackpop.features.game.data.GameConfigId
import jez.jetpackpop.features.game.model.GameScoreData
import kotlinx.parcelize.Parcelize

sealed class GameEndState: Parcelable {

    abstract val gameConfigId: GameConfigId
    abstract val remainingTime: Float
    abstract val score: GameScoreData
    abstract val didWin: Boolean

    @Parcelize
    data class LevelEndState(
        override val gameConfigId: GameConfigId,
        override val remainingTime: Float,
        override val score: GameScoreData,
        override val didWin: Boolean,
    ): GameEndState()

    @Parcelize
    data class ChapterEndState(
        override val gameConfigId: GameConfigId,
        override val remainingTime: Float,
        override val score: GameScoreData,
        override val didWin: Boolean,
        val previousScore: Int,
    ): GameEndState()
}
