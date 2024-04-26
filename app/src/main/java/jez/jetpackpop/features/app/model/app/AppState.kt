package jez.jetpackpop.features.app.model.app

import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.highscore.HighScores

data class AppState(
    val highScores: HighScores,
    val activeScreen: ActiveScreen,
    val activeGameConfig: GameConfiguration = GameConfiguration.Default,
    val nextGameConfiguration: GameConfiguration = GameConfiguration.Default,
    val hasWonActiveGame: Boolean = false,
) {
    companion object {
        val Default = AppState(
            highScores = HighScores.defaultValue,
            activeScreen = ActiveScreen.MainMenu,
        )
    }
}

enum class ActiveScreen {
    MainMenu,
    InGame,
    GameEnd,
    ChapterComplete,
    Victory,
}
