package jez.jetpackpop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.domain.AppLogic
import jez.jetpackpop.features.app.domain.GameLogic
import jez.jetpackpop.features.app.domain.GameLogicEvent
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppViewModelFactory(
    private val highScoresRepository: HighScoresRepository,
    private val soundManager: SoundManager,
    private val width: Float,
    private val height: Float,
    private val gameInputEventFlow: MutableSharedFlow<GameInputEvent>,
    private val appInputEventFlow: MutableSharedFlow<AppInputEvent>,
    private val gameOutputEventFlow: MutableSharedFlow<GameLogicEvent> = MutableSharedFlow(
        extraBufferCapacity = 1
    ),
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                runBlocking {
                    val highScores = highScoresRepository.highScoresFlow.first()
                    val appLogic = AppLogic(
                        highScores,
                        highScoresRepository,
                        gameInputEventFlow,
                    )
                    val gameLogic = GameLogic(
                        gameOutputEventFlow,
                        soundManager,
                        width,
                        height,
                    )
                    AppViewModel(
                        highScoresRepository,
                        appInputEventFlow,
                        gameInputEventFlow,
                        gameOutputEventFlow,
                        appLogic,
                        gameLogic
                    ) as T
                }
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
