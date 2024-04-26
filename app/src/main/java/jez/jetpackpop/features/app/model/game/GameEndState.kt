package jez.jetpackpop.features.app.model.game

import android.os.Parcelable
import jez.jetpackpop.features.app.domain.GameConfigId
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
    ): GameEndState()
}
