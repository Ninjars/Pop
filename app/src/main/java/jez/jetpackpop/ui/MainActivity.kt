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
import jez.jetpackpop.data.HighScoreDataSerializer
import jez.jetpackpop.data.HighScoresRepository
import jez.jetpackpop.model.GameViewModel
import jez.jetpackpop.model.GameViewModelFactory
import jez.jetpackpop.model.PopViewModel

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)
    private val appViewModel: PopViewModel by viewModels()
    private lateinit var gameViewModel: GameViewModel

    init {
        lifecycle.addObserver(soundManager)
    }

    private val Context.highScoresStore: DataStore<HighScoresProto> by dataStore(
        fileName = HIGHSCORES_DATASTORE_FILE_NAME,
        serializer = HighScoreDataSerializer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(
            this,
            GameViewModelFactory(HighScoresRepository(dataStore = this.highScoresStore))
        ).get(GameViewModel::class.java)

        setContent {
            App(
                soundManager,
                gameViewModel,
                appViewModel,
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
    }
}
