package jez.jetpackpop.features.highscore

import android.os.Parcelable
import jez.jetpackpop.features.game.data.GameChapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class HighScores(val chapterScores: Map<GameChapter, Int>) : Parcelable {
    companion object {
        val defaultValue = HighScores(emptyMap())
    }
}
