package jez.jetpackpop.features.app.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AppViewModel(
    appInputEventFlow: SharedFlow<AppInputEvent>
) : ViewModel() {
    private val _appState = MutableStateFlow<AppState>(AppState.InitialisingState)
    val appState: StateFlow<AppState> = _appState

    init {
        viewModelScope.launch {
            appInputEventFlow.collect {
                _appState.value = processInputEvent(it)
            }
        }
    }

    private fun processInputEvent(event: AppInputEvent): AppState =
        with(appState.value) {
            when (event) {
                else -> this
            }
        }

    fun onNewState(state: AppState) {
        _appState.value = state
    }
}
