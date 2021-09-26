package jez.jetpackpop.features.app.model

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.*
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(
    private val highScoresRepository: HighScoresRepository,
    initialHighScore: HighScores,
    appInputEventFlow: SharedFlow<AppInputEvent>,
    private val gameEventFlow: MutableSharedFlow<GameInputEvent>,
) : ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.MainMenuState(initialHighScore))
    val appState: StateFlow<AppState> = _appState

    init {
        viewModelScope.launch {
            appInputEventFlow.collect {
                _appState.value = processInputEvent(it)
            }
        }
    }

    private suspend fun processInputEvent(event: AppInputEvent): AppState =
        when (event) {
            is AppInputEvent.Navigation -> handleNavigation(event)
            is AppInputEvent.StartNewGame ->
                handleStartNewGame(event.config)
            is AppInputEvent.StartNextChapter ->
                handleNextChapter(event.config)
            is AppInputEvent.StartNextLevel ->
                handleNextLevel(event.config)
            is AppInputEvent.GameEnded -> handleGameEnd(event.gameEndState)
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
                Log.e("AppViewModel", "handleNavigation: MainMenu")
//                gameEventFlow.tryEmit(
//                    GameInputEvent.StartNewGame(demoConfiguration())
//                )
                highScoresRepository.highScoresFlow.first().let {
                    Log.e("AppViewModel", "handleNavigation: MainMenu : DONE: $it")
                    AppState.MainMenuState(it)
                }
//                .also {
//                    Log.e("AppViewModel", "handleNavigation: MainMenu : DONE")
//                }
            }
        }

    fun handleBackPressed(): Boolean =
        if (appState.value is AppState.MainMenuState) {
            false
        } else {
            viewModelScope.launch {
                _appState.value = processInputEvent(AppInputEvent.Navigation.MainMenu)
            }
            true
        }
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
                clickable = false,
            )
        ),
        isLastInChapter = false,
    )
