package jez.jetpackpop.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_GAME
import android.media.SoundPool
import jez.jetpackpop.R

enum class GameSoundEffect {
    TARGET_TAPPED,
}

class GameSoundEffectPlayer {
    private lateinit var soundPool: SoundPool
    private var soundIds: List<Int> = emptyList()

    fun initialise(context: Context) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(USAGE_GAME).build())
            .build()

        soundIds = GameSoundEffect.values()
            .map {
                when (it) {
                    GameSoundEffect.TARGET_TAPPED -> R.raw.bubblepop
                }
            }.map {
                soundPool.load(context, it, 1)
            }
    }

    fun tearDown() {
        soundPool.release()
    }

    fun play(effect: GameSoundEffect, pitchVariance: Float = 0.2f) {
        val soundId = soundIds[effect.ordinal]
        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            (1 - pitchVariance) + Math.random().toFloat() * pitchVariance * 2
        )
    }
}
