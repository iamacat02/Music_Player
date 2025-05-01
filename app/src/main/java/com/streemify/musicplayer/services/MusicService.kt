package com.streemify.musicplayer.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException

class MusicService : Service() {

    private val musicBinder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    private var onCompletionListener: (() -> Unit)? = null

    override fun onBind(intent: Intent?): IBinder {
        return musicBinder
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    // Play Audio Method
    fun play(audioPath: String, onCompletion: (() -> Unit)? = null) {
        this.onCompletionListener = onCompletion
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()

            mediaPlayer?.setDataSource(audioPath)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener {
                it.start()
                Log.d("MusicService", "Audio is playing: $audioPath")
            }

            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                Log.e("MusicService", "Error during playback. What: $what, Extra: $extra")
                false
            }

            // Add completion listener to auto-play next track
            mediaPlayer?.setOnCompletionListener {
                onCompletionListener?.invoke() // Trigger the next track
                Log.d("MusicService", "Audio completed, moving to next")
            }

        } catch (e: IOException) {
            Log.e("MusicService", "Error loading audio", e)
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        Log.d("MusicService", "Audio paused")
    }

    fun resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            Log.d("MusicService", "Audio resumed")
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        Log.d("MusicService", "Audio stopped")
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        Log.d("MusicService", "MediaPlayer released")
    }
}