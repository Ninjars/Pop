package jez.jetpackpop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import jez.jetpackpop.audio.SoundManager

class MainActivity : ComponentActivity() {
    private val soundManager = SoundManager(this)

    init {
        lifecycle.addObserver(soundManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(soundManager)
        }
    }
}
