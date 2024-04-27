package jez.jetpackpop.features.app.domain

import jez.jetpackpop.features.app.model.app.ActiveScreen
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
import jez.jetpackpop.features.app.model.game.GameEndState
import jez.jetpackpop.features.app.model.game.GameInputEvent
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
    private val _appState = MutableStateFlow(AppState.Default.copy(highScores = initialHighScore))
    val appState: StateFlow<AppState> = _appState

    suspend fun processInputEvent(event: AppInputEvent) {
        with(_appState.value) {
            _appState.value = when (event) {
                is AppInputEvent.Navigation -> handleNavigation(event)
                is AppInputEvent.StartNewGame ->
                    handleStartNewGame(this, event.config)

                is AppInputEvent.StartNextChapter ->
                    handleNextChapter(this, event.config)

                is AppInputEvent.StartNextLevel ->
                    handleNextLevel(this, event.config)

                is AppInputEvent.RestartLevel ->
                    handleRestartLevel(this, event.config)

                is AppInputEvent.GameEnded -> handleGameEnd(this, event.gameEndState)
            }
        }
    }

    private fun handleNextChapter(
        appState: AppState,
        gameConfiguration: GameConfiguration,
    ): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNextChapter(gameConfiguration)
        )
        return appState.copy(
            activeScreen = ActiveScreen.InGame,
            activeGameConfig = gameConfiguration,
        )
    }

    private fun handleNextLevel(
        appState: AppState,
        gameConfiguration: GameConfiguration,
    ): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNextLevel(gameConfiguration)
        )
        return appState.copy(
            activeScreen = ActiveScreen.InGame,
            activeGameConfig = gameConfiguration,
        )
    }

    private fun handleRestartLevel(
        appState: AppState,
        gameConfiguration: GameConfiguration,
    ): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.RestartLevel(gameConfiguration)
        )
        return appState.copy(
            activeScreen = ActiveScreen.InGame,
            activeGameConfig = gameConfiguration,
        )
    }

    private fun handleStartNewGame(
        appState: AppState,
        gameConfiguration: GameConfiguration,
    ): AppState {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNewGame(gameConfiguration)
        )
        return appState.copy(
            activeScreen = ActiveScreen.InGame,
            activeGameConfig = gameConfiguration,
        )
    }

    private fun demoConfiguration(): GameConfiguration =
        GameConfiguration(
            id = GameConfigId(GameChapter.SIMPLE_SINGLE, -1),
            isDemo = true,
            timeLimitSeconds = -1f,
            targetConfigurations = listOf(
                TargetConfiguration(
                    type = TargetType.TARGET,
                    radius = 30f,
                    count = 10,
                    minSpeed = 8f,
                    maxSpeed = 16f,
                    clickResult = null,
                )
            ),
            isLastInChapter = false,
        )

    private fun handleGameEnd(appState: AppState, gameEndState: GameEndState): AppState {
        val nextGame =
            if (gameEndState.didWin) {
                getNextGameConfiguration(gameEndState.gameConfigId)
            } else {
                getGameConfiguration(gameEndState.gameConfigId)
            }

        return when {
            nextGame == null ->
                appState.copy(activeScreen = ActiveScreen.Victory)

            gameEndState.gameConfigId.chapter != nextGame.id.chapter ->
                appState.copy(
                    activeScreen = ActiveScreen.ChapterComplete,
                    activeGameConfig = getGameConfiguration(gameEndState.gameConfigId)!!,
                    nextGameConfiguration = nextGame,
                )

            else ->
                appState.copy(
                    activeScreen = ActiveScreen.GameEnd,
                    nextGameConfiguration = nextGame,
                    hasWonActiveGame = gameEndState.didWin,
                )
        }
    }

    private suspend fun handleNavigation(event: AppInputEvent.Navigation): AppState =
        when (event) {
            is AppInputEvent.Navigation.MainMenu -> {
                startDemoGame()

                highScoresRepository.highScoresFlow.first().let {
                    AppState(
                        highScores = it,
                        activeScreen = ActiveScreen.MainMenu,
                        activeGameConfig = demoConfiguration(),
                    )
                }
            }
        }

    fun startDemoGame() {
        gameEventFlow.tryEmit(
            GameInputEvent.StartNewGame(demoConfiguration())
        )
    }
}
