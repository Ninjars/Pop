package jez.jetpackpop.features.app.model

sealed class AppInputEvent {
    sealed class Navigation : AppInputEvent() {
        object MainMenu : Navigation()
    }
}
