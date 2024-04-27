package jez.jetpackpop.audio

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import jez.jetpackpop.audio.SoundManager.SoundVariance

interface SoundManager : LifecycleEventObserver {
    /**
     * Plays an indicated sound effect with a random small pitch adjustment
     */
    fun playEffect(effect: GameSoundEffect, variance: SoundVariance)

    /**
     * Plays an indicated sound effect without any pitch adjustment
     */
    fun playSound(effect: GameSoundEffect)

    enum class SoundVariance {
        Low,
        High
    }
}

class NoOpSoundManager : SoundManager {
    override fun playEffect(effect: GameSoundEffect, variance: SoundVariance) {
    }

    override fun playSound(effect: GameSoundEffect) {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    }
}

class SoundManagerImpl(private val context: Context) : SoundManager {

    private val popEffectPlayer = GameSoundEffectPlayer()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> initialise()
            Lifecycle.Event.ON_PAUSE -> tearDown()
            else -> Unit
        }
    }

    private fun initialise() {
        popEffectPlayer.initialise(context)
    }

    private fun tearDown() {
        popEffectPlayer.tearDown()
    }

    /**
     * Plays an indicated sound effect with a random small pitch adjustment
     */
    override fun playEffect(effect: GameSoundEffect, variance: SoundVariance) {
        popEffectPlayer.play(
            effect = effect,
            pitchVariance = when (variance) {
                SoundVariance.Low -> 0.05f
                SoundVariance.High -> 0.2f
            }
        )
    }

    /**
     * Plays an indicated sound effect without any pitch adjustment
     */
    override fun playSound(effect: GameSoundEffect) {
        popEffectPlayer.play(effect, 0f)
    }
}
