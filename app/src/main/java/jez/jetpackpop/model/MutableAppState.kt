package jez.jetpackpop.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MutableAppState {
    var state by mutableStateOf<AppState>(AppState.InitialisingState)
}
