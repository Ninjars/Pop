package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AppViewModel(
    appInputEventFlow: SharedFlow<AppInputEvent>,
    private val appLogic: AppLogic,
) : ViewModel() {

    val appState: StateFlow<AppState> = appLogic.appState

    init {
        viewModelScope.launch {
            appInputEventFlow.collect {
                appLogic.processInputEvent(it)
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
}
