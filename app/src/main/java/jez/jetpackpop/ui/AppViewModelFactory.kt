package jez.jetpackpop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.features.app.model.AppInputEvent
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.SharedFlow

class AppViewModelFactory (
    private val highScoresRepository: HighScoresRepository,
    private val gameInputEventFlow: SharedFlow<GameInputEvent>,
    private val appInputEventFlow: SharedFlow<AppInputEvent>,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(highScoresRepository, gameInputEventFlow) as T

            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(appInputEventFlow) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
