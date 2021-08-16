package jez.jetpackpop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameEndState(
    val gameConfigId: GameConfigId,
    val remainingTime: Float,
    val score: Int,
    val didWin: Boolean,
): Parcelable