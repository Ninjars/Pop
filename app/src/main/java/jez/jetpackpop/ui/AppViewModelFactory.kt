package jez.jetpackpop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class AppViewModelFactory (
    private val highScoresRepository: HighScoresRepository,
    private val gameInputEventFlow: MutableSharedFlow<GameInputEvent>,
    private val appInputEventFlow: MutableSharedFlow<AppInputEvent>,
    private val width: Float,
    private val height: Float
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(highScoresRepository, gameInputEventFlow, appInputEventFlow, width, height) as T

            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(highScoresRepository, appInputEventFlow, gameInputEventFlow) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
