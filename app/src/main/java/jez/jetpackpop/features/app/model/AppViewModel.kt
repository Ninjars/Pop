package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.app.domain.AppLogic
import jez.jetpackpop.features.app.domain.GameLogic
import jez.jetpackpop.features.app.domain.GameLogicEvent
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
import jez.jetpackpop.features.app.model.game.GameEndState
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val highScoresRepository: HighScoresRepository,
    appInputEventFlow: MutableSharedFlow<AppInputEvent>,
    gameInputEvents: SharedFlow<GameInputEvent>,
    gameOutputEventFlow: SharedFlow<GameLogicEvent>,
    private val appLogic: AppLogic,
    gameLogic: GameLogic,
) : ViewModel() {
    val gameState: StateFlow<GameState> = gameLogic.gameState
    val appState: StateFlow<AppState> = appLogic.appState

    init {
        viewModelScope.launch {
            appInputEventFlow.collect {
                appLogic.processInputEvent(it)
            }
        }

        viewModelScope.launch {
            gameInputEvents.collect {
                gameLogic.processInputEvent(it)
            }
        }

        viewModelScope.launch {
            gameOutputEventFlow.collect {
                when (it) {
                    is GameLogicEvent.GameEnded ->
                        appInputEventFlow.tryEmit(
                            AppInputEvent.GameEnded(
                                it.gameEndState
                            )
                        )
                }
            }
        }

        viewModelScope.launch {
            gameLogic.outputEvents.collect {
                when (it) {
                    is GameLogicEvent.GameEnded -> {
                        val state = it.gameEndState
                        if (state.didWin) {
                            recordGameEnd(state)
                        }
                    }
                }
            }
        }

        appLogic.startDemoGame()
    }

    private fun recordGameEnd(
        state: GameEndState,
    ) {
        viewModelScope.launch {
            highScoresRepository.recordEndOfLevel(
                chapterName = state.gameConfigId.chapter.persistenceName,
                levelIndex = state.gameConfigId.id,
                levelScore = state.score.gameScore,
                timeRemaining = state.remainingSeconds,
                totalChapterScore = state.score.totalScore,
            )
        }
    }
}
