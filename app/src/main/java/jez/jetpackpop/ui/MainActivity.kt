package jez.jetpackpop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import jez.jetpackpop.R
import jez.jetpackpop.dependency.PopServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        PopServices.popSoundEffects.initialise(
            this,
            listOf(
                R.raw.pop1,
                R.raw.pop2,
                R.raw.pop3,
                R.raw.pop4,
            )
        )
    }

    override fun onStop() {
        PopServices.popSoundEffects.tearDown()
        super.onStop()
    }
}
