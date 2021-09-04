package jez.jetpackpop.data

import android.os.Parcelable
import jez.jetpackpop.model.GameChapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class HighScores(val chapterScores: Map<GameChapter, Int>) : Parcelable {
    companion object {
        val defaultValue = HighScores(emptyMap())
    }
}
