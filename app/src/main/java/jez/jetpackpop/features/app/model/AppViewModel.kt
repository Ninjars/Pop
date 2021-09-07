package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel : ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.InitialisingState)
    val appState: StateFlow<AppState> = _appState

    fun onNewState(state: AppState) {
        _appState.value = state
    }
}
