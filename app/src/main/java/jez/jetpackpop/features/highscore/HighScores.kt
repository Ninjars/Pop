package jez.jetpackpop.features.highscore

import androidx.compose.runtime.Immutable
import jez.jetpackpop.features.app.domain.GameChapter

@Immutable
data class HighScores(
    val chapterScores: Map<GameChapter, Int>,
    val levelScores: Map<GameChapter, List<LevelScore>>,
) {

    data class LevelScore(
        val level: Int,
        val highestScore: Int,
        val mostSecondsRemaining: Int,
    )

    companion object {
        val defaultValue = HighScores(emptyMap(), emptyMap())
    }
}
