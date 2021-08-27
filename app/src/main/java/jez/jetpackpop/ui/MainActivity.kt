package jez.jetpackpop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.model.PopViewModel

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)
    private val viewModel: PopViewModel by viewModels()

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(
                soundManager,
                viewModel,
            ) {
                viewModel.onNewState(it)
            }
        }
    }
}
