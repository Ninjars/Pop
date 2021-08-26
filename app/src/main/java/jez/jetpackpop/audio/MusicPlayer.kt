package jez.jetpackpop.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log

class MusicPlayer(private val context: Context, private val musicResourceIds: List<Int>) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0

    fun tearDown() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Start playing music. Will loop through tracks until stop is invoked.
     */
    fun play() {
        if (musicResourceIds.isEmpty()) {
            Log.w("MusicPlayer", "play() invoked with empty musicResourceIds")
            return
        }

        val player = mediaPlayer
        if (player == null) {
            mediaPlayer = MediaPlayer.create(context, musicResourceIds[currentIndex % musicResourceIds.size]).apply {
                setOnCompletionListener {
                    currentIndex++
                    prepareTrack(context, it, musicResourceIds[currentIndex % musicResourceIds.size])
                }
                setOnPreparedListener { onPrepared(it) }
            }
        } else {
            player.start()
        }
    }

    private fun prepareTrack(context: Context, player: MediaPlayer, resourceId: Int) {
        val descriptor = context.resources.openRawResourceFd(resourceId) ?: return
        player.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        descriptor.close()
        player.prepareAsync()
    }

    private fun onPrepared(player: MediaPlayer) {
        player.start()
    }

    fun stop() {
        mediaPlayer?.pause()
    }
}
