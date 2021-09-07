package jez.jetpackpop.features.app.model

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.game.GameEndState
import jez.jetpackpop.features.game.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AppViewModel(
    appInputEventFlow: SharedFlow<AppInputEvent>
) : ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.InitialisingState)
    val appState: StateFlow<AppState> = _appState

    init {
        viewModelScope.launch {
            appInputEventFlow.collect {
                _appState.value = processInputEvent(it)
            }
        }
    }

    private fun processInputEvent(event: AppInputEvent): AppState =
        with(appState.value) {
            when (event) {
                is AppInputEvent.Navigation -> handleNavigation(event)
                is AppInputEvent.StartGameFromChapter ->
                    AppState.StartGameState(
                        getFirstGameConfiguration(event.gameChapter),
                        isNewChapter = true,
                        isNewGame = true,
                    )
                is AppInputEvent.StartGame ->
                    AppState.StartGameState(
                        event.config,
                        isNewChapter = true,
                        isNewGame = false,
                    )
                is AppInputEvent.GameEnded -> handleGameEnd(event.gameEndState)
            }
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
                AppState.ChapterCompleteMenuState(gameEndState.gameConfigId, nextGame, gameEndState.score)

            else ->
                AppState.EndMenuState(nextGame, gameEndState.didWin, gameEndState.score)
        }
    }

    private fun handleNavigation(event: AppInputEvent.Navigation): AppState =
        when (event) {
            is AppInputEvent.Navigation.MainMenu -> AppState.MainMenuState(demoConfiguration())
        }

    fun onNewState(state: AppState) {
        _appState.value = state
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
