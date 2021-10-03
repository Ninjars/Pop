package jez.jetpackpop.features.app.model.app

import android.os.Parcelable
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.domain.GameConfigId
import jez.jetpackpop.features.app.model.game.GameScoreData
import jez.jetpackpop.features.highscore.HighScores
import kotlinx.parcelize.Parcelize

sealed class AppState : Parcelable {
    @Parcelize
    data class MainMenuState(val highScores: HighScores) : AppState()

    @Parcelize
    object InGameState : AppState()

    @Parcelize
    data class EndMenuState(
        val nextGameConfiguration: GameConfiguration,
        val didWin: Boolean,
        val score: GameScoreData,
    ) : AppState()

    @Parcelize
    data class ChapterCompleteMenuState(
        val completedChapterId: GameConfigId,
        val nextGameConfiguration: GameConfiguration,
        val score: GameScoreData,
    ) : AppState()

    @Parcelize
    object VictoryMenuState : AppState()
}
