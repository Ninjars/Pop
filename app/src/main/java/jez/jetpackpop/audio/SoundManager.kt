package jez.jetpackpop.audio

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import jez.jetpackpop.R

class SoundManager(private val context: Context) : LifecycleEventObserver {

    private val popEffectPlayer = RandomSoundEffectPlayer()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> initialise()
            Lifecycle.Event.ON_PAUSE -> tearDown()
            else -> Unit
        }
    }

    private fun initialise() {
        popEffectPlayer.initialise(
            context,
            listOf(
                R.raw.bubblepop,
            )
        )
    }

    private fun tearDown() {
        popEffectPlayer.tearDown()
    }

    fun playPop() {
        Log.w("SoundManager", "playPop")
        popEffectPlayer.play()
    }
}
