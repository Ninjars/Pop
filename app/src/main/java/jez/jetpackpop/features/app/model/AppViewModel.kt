package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.app.domain.*
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.app.AppState
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.app.model.game.GameScoreData
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.max

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
            highScoresRepository.highScoresFlow.collect {
                gameLogic.processInputEvent(GameInputEvent.NewHighScore(it))
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
                                it.highScores,
                                state.score,
                                it.config,
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
        highScores: HighScores,
        scoreData: GameScoreData,
        config: GameConfiguration,
    ) {
        viewModelScope.launch {
            highScoresRepository.updateHighScores(
                highScores.copy(
                    chapterScores = highScores.chapterScores.run {
                        val chapter = config.id.chapter
                        toMutableMap().apply {
                            this[chapter] =
                                max(getOrDefault(chapter, 0), scoreData.totalScore)

                            if (config.isLastInChapter) {
                                chapter.getNextChapter()?.also {
                                    this[it] = getOrDefault(it, 0)
                                }
                            }
                        }
                    }
                )
            )
        }
    }
}
