package jez.jetpackpop.features.app.model

import android.os.Parcelable
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.game.data.GameConfigId
import jez.jetpackpop.features.game.model.GameScoreData
import jez.jetpackpop.features.highscore.HighScores
import kotlinx.parcelize.Parcelize

sealed class AppState : Parcelable {
    @Parcelize
    object Loading : AppState()

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
