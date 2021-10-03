package jez.jetpackpop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameLogic
import jez.jetpackpop.features.game.model.GameLogicEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppViewModelFactory (
    private val highScoresRepository: HighScoresRepository,
    private val width: Float,
    private val height: Float,
    private val gameInputEventFlow: MutableSharedFlow<GameInputEvent>,
    private val appInputEventFlow: MutableSharedFlow<AppInputEvent>,
    private val gameOutputEventFlow: MutableSharedFlow<GameLogicEvent> = MutableSharedFlow(extraBufferCapacity = 1),
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                val gameLogic = GameLogic(
                    gameOutputEventFlow,
                    width,
                    height,
                )
                GameViewModel(
                    highScoresRepository,
                    gameInputEventFlow,
                    gameOutputEventFlow,
                    appInputEventFlow,
                    gameLogic,
                ) as T
            }

            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                runBlocking {
                    val highScores = highScoresRepository.highScoresFlow.first()
                    AppViewModel(highScoresRepository, highScores, appInputEventFlow, gameInputEventFlow) as T
                }
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
