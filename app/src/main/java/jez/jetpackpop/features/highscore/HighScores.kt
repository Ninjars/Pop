package jez.jetpackpop.features.highscore

import android.os.Parcelable
import jez.jetpackpop.features.app.domain.GameChapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class HighScores(val chapterScores: Map<GameChapter, Int>) : Parcelable {
    companion object {
        val defaultValue = HighScores(emptyMap())
    }
}
