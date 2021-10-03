package jez.jetpackpop.features.app.model

import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.*
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class AppLogic(
    initialHighScore: HighScores,
    private val highScoresRepository: HighScoresRepository,
    private val gameEventFlow: MutableSharedFlow<GameInputEvent>,
) {
    private val _appState = MutableStateFlow<AppState>(AppState.MainMenuState(initialHighScore))
    val appState: StateFlow<AppState> = _appState

    suspend fun processInputEvent(event: AppInputEvent) {
        _appState.value = when (event) {
            is AppInputEvent.Navigation -> handleNavigation(event)
            is AppInputEvent.StartNewGame ->
                handleStartNewGame(event.config)
            is AppInputEvent.StartNextChapter ->
                handleNextChapter(event.config)
            is AppInputEvent.StartNextLevel ->
                handleNextLevel(event.config)
            is AppInputEvent.GameEnded -> handleGameEnd(event.gameEndState)
        }
    }

    private fun handleNextChapter(gameConfiguration: GameConfiguration): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNextChapter(gameConfiguration)
        )
        return AppState.InGameState
    }

    private fun handleNextLevel(gameConfiguration: GameConfiguration): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNextLevel(gameConfiguration)
        )
        return AppState.InGameState
    }

    private fun handleStartNewGame(
        gameConfiguration: GameConfiguration,
    ): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNewGame(gameConfiguration)
        )
        return AppState.InGameState
    }

    fun startDemoGame() {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNewGame(demoConfiguration())
        )
    }

    private fun demoConfiguration(): GameConfiguration =
        GameConfiguration(
            id = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
            isDemo = true,
            timeLimitSeconds = -1f,
            targetConfigurations = listOf(
                TargetConfiguration(
                    color = TargetColor.TARGET,
                    radius = 30.dp,
                    count = 10,
                    minSpeed = 8.dp,
                    maxSpeed = 16.dp,
                    clickResult = null,
                )
            ),
            isLastInChapter = false,
        )

    private fun handleGameEnd(gameEndState: GameEndState): AppState {
        val nextGame =
            if (gameEndState.didWin) {
                getNextGameConfiguration(gameEndState.gameConfigId)
            } else {
                getGameConfiguration(gameEndState.gameConfigId)
            }

        return when {
            nextGame == null ->
                AppState.VictoryMenuState

            gameEndState.gameConfigId.chapter != nextGame.id.chapter ->
                AppState.ChapterCompleteMenuState(
                    gameEndState.gameConfigId,
                    nextGame,
                    gameEndState.score
                )

            else ->
                AppState.EndMenuState(nextGame, gameEndState.didWin, gameEndState.score)
        }
    }

    private suspend fun handleNavigation(event: AppInputEvent.Navigation): AppState =
        when (event) {
            is AppInputEvent.Navigation.MainMenu -> {
                startDemoGame()
                highScoresRepository.highScoresFlow.first().let {
                    AppState.MainMenuState(it)
                }
            }
        }
}