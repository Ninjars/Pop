package jez.jetpackpop.features.app.model.game

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameScoreData(
    val startingScore: Int,
    val tapHistory: List<Boolean>,
    val gameScore: Int,
    val currentMultiplier: Int,
) : Parcelable {
    @IgnoredOnParcel
    val totalScore: Int = startingScore + gameScore
}