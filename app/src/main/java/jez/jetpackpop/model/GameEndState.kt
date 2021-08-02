package jez.jetpackpop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameEndState(
    val remainingTime: Float,
    val score: Int,
    val didWin: Boolean,
): Parcelable