package jez.jetpackpop.features.game.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.game.data.GameConfiguration
import jez.jetpackpop.features.highscore.HighScores
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.max

class GameViewModel(
    private val highScoresRepository: HighScoresRepository,
    gameInputEvents: SharedFlow<GameInputEvent>,
    gameOutputEventFlow: SharedFlow<GameLogicEvent>,
    appInputEventFlow: MutableSharedFlow<AppInputEvent>,
    gameLogic: GameLogic,
) : ViewModel() {
    val gameState: StateFlow<GameState> = gameLogic.gameState

    init {
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
