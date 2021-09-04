package jez.jetpackpop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.model.GameViewModel
import jez.jetpackpop.model.PopViewModel

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)
    private val appViewModel: PopViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
