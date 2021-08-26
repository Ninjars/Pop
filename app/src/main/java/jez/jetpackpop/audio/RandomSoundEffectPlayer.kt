package jez.jetpackpop.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_GAME
import android.media.SoundPool
import android.util.Log

class RandomSoundEffectPlayer {
    private lateinit var soundPool: SoundPool
    private var soundIds: List<Int> = emptyList()

    fun initialise(context: Context, soundResources: List<Int>) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(USAGE_GAME).build())
            .build()

        soundIds = soundResources.map {
            soundPool.load(context, it, 1)
        }
    }

    fun tearDown() {
        soundPool.release()
    }

    /**
     * Attempt to play a random sound effect.
     */
    fun play() {
        if (soundIds.isEmpty()) {
            Log.w("RandomSoundEffectPlayer", "play() invoked with empty sound effect list")
            return
        }

        val soundId = soundIds.random()
        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            0.8f + Math.random().toFloat() * 0.4f
        )
    }
}
