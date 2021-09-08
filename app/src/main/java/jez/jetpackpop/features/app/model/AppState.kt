package jez.jetpackpop.features.app.model

import android.os.Parcelable
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.game.data.GameConfigId
import jez.jetpackpop.features.game.model.GameScoreData
import kotlinx.parcelize.Parcelize

sealed class AppState : Parcelable {
    @Parcelize
    object InitialisingState : AppState()

    @Parcelize
    data class MainMenuState(
        val gameConfiguration: GameConfiguration,
    ) : AppState()

    @Parcelize
    data class StartGameState(
        val gameConfiguration: GameConfiguration,
        val isNewChapter: Boolean,
        val isNewGame: Boolean,
    ) : AppState()

    @Parcelize
    data class EndMenuState(
        val nextGameConfiguration: GameConfiguration,
        val didWin: Boolean,
        val score: GameScoreData,
    ) : AppState()

    @Parcelize
    data class ChapterCompleteMenuState(
        val completedChapterId: GameConfigId,
        val nextGame: GameConfiguration,
        val score: GameScoreData,
    ) : AppState()

    @Parcelize
    object VictoryMenuState : AppState()
}
