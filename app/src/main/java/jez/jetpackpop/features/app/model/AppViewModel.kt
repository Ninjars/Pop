package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.app.domain.AppLogic
import jez.jetpackpop.features.app.domain.GameChapter
import jez.jetpackpop.features.app.domain.GameLogic
import jez.jetpackpop.features.app.domain.GameLogicEvent
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
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
                            recordCurrentScore(
                                it.config.id.chapter,
                                state.score.totalScore,
                            )
                        }
                    }
                }
            }
        }

        appLogic.startDemoGame()
    }

    fun handleBackPressed(): Boolean =
        if (appState.value is AppState.MainMenuState) {
            false
        } else {
            viewModelScope.launch {
                appLogic.processInputEvent(AppInputEvent.Navigation.MainMenu)
            }
            true
        }

    private fun recordCurrentScore(
        chapter: GameChapter,
        score: Int,
    ) {
        viewModelScope.launch {
            highScoresRepository.updateScore(chapter.persistenceName, score)
        }
    }
}
