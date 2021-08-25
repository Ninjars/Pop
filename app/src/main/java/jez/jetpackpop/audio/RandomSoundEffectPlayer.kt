package jez.jetpackpop.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class RandomSoundEffectPlayer {
    private var managedMediaPlayers: List<MediaPlayer> = emptyList()
    private var currentIndex = 0

    fun initialise(context: Context, countPerSound: Int, soundResources: List<Int>) {
        managedMediaPlayers = soundResources.flatMap {
            (0 until countPerSound).map {
                MediaPlayer.create(context, it).also { player -> player.prepareAsync() }
            }
        }.shuffled()
    }

    fun tearDown() {
        for (player in managedMediaPlayers) {
            player.release()
        }
        managedMediaPlayers = emptyList()
    }

    /**
     * Attempt to play a sound effect. If no player is available or all players are current playing
     * then there will be no effect.
     */
    fun play() {
        if (managedMediaPlayers.isEmpty()) {
            Log.w("RandomSoundEffectPlayer", "play() invoked with empty managedMediaPlayers")
            return
        }

        for (i in managedMediaPlayers.indices) {
            val index = (currentIndex + i) % managedMediaPlayers.size
            val player = managedMediaPlayers[index]
            if (!player.isPlaying) {
                player.seekTo(0)
                player.start()
                currentIndex = index
                break
            }
        }
    }
}
