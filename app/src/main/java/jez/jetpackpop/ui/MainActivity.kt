package jez.jetpackpop.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.HighScoresProto
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.audio.SoundManagerImpl
import jez.jetpackpop.features.app.model.app.AppInputEvent
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.app.ui.App
import jez.jetpackpop.features.app.model.game.GameInputEvent
import jez.jetpackpop.features.highscore.HighScoreDataSerializer
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {
    private val soundManager: SoundManager = SoundManagerImpl(this)
    private val gameEventFlow = MutableSharedFlow<GameInputEvent>(extraBufferCapacity = 5)
    private val appEventFlow = MutableSharedFlow<AppInputEvent>(extraBufferCapacity = 5)
    private lateinit var appViewModel: AppViewModel

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BoxWithConstraints {
                val viewModelFactory = ViewModelProvider(
                    this@MainActivity,
                    AppViewModelFactory(
                        HighScoresRepository(
                            dataStore = this@MainActivity.highScoresStore
                        ),
                        maxWidth.value,
                        maxHeight.value,
                        gameEventFlow,
                        appEventFlow,
                    )
                )
                appViewModel = viewModelFactory.get(AppViewModel::class.java)
                App(
                    soundManager,
                    appViewModel,
                    appEventFlow,
                    gameEventFlow,
                )
            }

            LaunchedEffect(Unit) {
                appEventFlow.tryEmit(AppInputEvent.Navigation.MainMenu)
                runGameLoop(gameEventFlow)
            }
        }
    }

    private suspend fun runGameLoop(
        gameEventFlow: MutableSharedFlow<GameInputEvent>,
    ) {
        var lastFrame = 0L
        while (true) {
            val nextFrame = awaitFrame() / 1000_000L
            if (lastFrame != 0L) {
                val deltaMillis = nextFrame - lastFrame
                gameEventFlow.emit(GameInputEvent.Update(deltaMillis / 1000f))
            }
            lastFrame = nextFrame
        }
    }

    override fun onPause() {
        gameEventFlow.tryEmit(GameInputEvent.Pause)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        gameEventFlow.tryEmit(GameInputEvent.Resume)
    }

    override fun onBackPressed() {
        if (!appViewModel.handleBackPressed()) {
            super.onBackPressed()
        } else {
            soundManager.playSound(GameSoundEffect.BACK_INVOKED)
        }
    }

    private companion object {
        const val HIGHSCORES_DATASTORE_FILE_NAME = "highscores.pb"

        val Context.highScoresStore: DataStore<HighScoresProto> by dataStore(
            fileName = HIGHSCORES_DATASTORE_FILE_NAME,
            serializer = HighScoreDataSerializer
        )
    }
}
