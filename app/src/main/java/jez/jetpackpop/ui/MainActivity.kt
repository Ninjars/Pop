package jez.jetpackpop.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import jez.jetpackpop.HighScoresProto
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.features.app.model.PopViewModel
import jez.jetpackpop.features.app.ui.App
import jez.jetpackpop.features.game.model.GameInputEvent
import jez.jetpackpop.features.game.model.GameViewModel
import jez.jetpackpop.features.game.model.GameViewModelFactory
import jez.jetpackpop.features.highscore.HighScoreDataSerializer
import jez.jetpackpop.features.highscore.HighScoresRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)
    private val appViewModel: PopViewModel by viewModels()
    private val gameEventFlow = MutableSharedFlow<GameInputEvent>(extraBufferCapacity = 5)
    private lateinit var gameViewModel: GameViewModel

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(
            this,
            GameViewModelFactory(
                HighScoresRepository(dataStore = this.highScoresStore),
                gameEventFlow,
            )
        ).get(GameViewModel::class.java)

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
        gameViewModel.onLifecyclePause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.onLifecycleResume()
    }

    private companion object {
        const val HIGHSCORES_DATASTORE_FILE_NAME = "highscores.pb"

        val Context.highScoresStore: DataStore<HighScoresProto> by dataStore(
            fileName = HIGHSCORES_DATASTORE_FILE_NAME,
            serializer = HighScoreDataSerializer
        )
    }
}
