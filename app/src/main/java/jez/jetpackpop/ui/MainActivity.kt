package jez.jetpackpop.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.HighScoresProto
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.AppViewModel
import jez.jetpackpop.features.app.ui.App
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.highscore.HighScoreDataSerializer
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)
    private val gameEventFlow = MutableSharedFlow<GameInputEvent>(extraBufferCapacity = 5)
    private lateinit var appViewModel: AppViewModel
    private lateinit var gameViewModel: GameViewModel

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = ViewModelProvider(
            this,
            AppViewModelFactory(
                HighScoresRepository(dataStore = this.highScoresStore),
                gameEventFlow,
            )
        )
        appViewModel = viewModelFactory.get(AppViewModel::class.java)
        gameViewModel = viewModelFactory.get(GameViewModel::class.java)

        setContent {
            App(
                soundManager,
                gameViewModel,
                appViewModel,
                gameEventFlow,
            ) {
                appViewModel.onNewState(it)
            }
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

    private companion object {
        const val HIGHSCORES_DATASTORE_FILE_NAME = "highscores.pb"

        val Context.highScoresStore: DataStore<HighScoresProto> by dataStore(
            fileName = HIGHSCORES_DATASTORE_FILE_NAME,
            serializer = HighScoreDataSerializer
        )
    }
}
