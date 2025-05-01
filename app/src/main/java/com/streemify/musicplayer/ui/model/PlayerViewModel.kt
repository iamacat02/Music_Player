package com.streemify.musicplayer.ui.model

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.streemify.musicplayer.services.MusicService
import com.streemify.musicplayer.ui.data.Audio
import com.streemify.musicplayer.ui.utils.ThumbnailLoader

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var musicService: MusicService? = null
    private var serviceBound = false
    private var audioList: List<Audio> = emptyList()
    private var currentIndex = -1

    private val _title = MutableLiveData<String>()
    private val _artist = MutableLiveData<String>()
    private val _duration = MutableLiveData<Int>()
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition
    private val _thumbnail = MutableLiveData<Bitmap>()
    val thumbnail: LiveData<Bitmap> get() = _thumbnail
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _currentAudioTrack = MutableLiveData<Audio?>()
    val currentAudioTrack: LiveData<Audio?> get() = _currentAudioTrack

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekRunnable = object : Runnable {
        override fun run() {
            try {
                musicService?.let { service ->
                    if (service.isPlaying()) {
                        _currentPosition.postValue(service.getCurrentPosition())
                        handler.postDelayed(this, 1000)
                    }
                }
            } catch (e: IllegalStateException) {
                Log.e("PlayerViewModel", "MediaPlayer error: ${e.message}")
                handler.removeCallbacks(this)
            }
        }
    }

    // -------- Playlist Support --------
    fun setAudioList(list: List<Audio>) {
        audioList = list
    }

    fun setCurrentSong(audio: Audio) {
        currentIndex = audioList.indexOfFirst { it.path == audio.path }
        if (currentIndex != -1) {
            _currentAudioTrack.value = audio
            _title.value = audio.title
            _artist.value = audio.artist
            _duration.value = audio.duration.toInt()
            _thumbnail.value = ThumbnailLoader.getThumbnail(getApplication(), audio.path)
            if (serviceBound) {
                playAudio(audio)
            }
        }
    }

    fun playNext() {
        if (currentIndex + 1 < audioList.size) {
            currentIndex++
            setCurrentSong(audioList[currentIndex])
        }
    }

    fun playPrevious() {
        if (currentIndex - 1 >= 0) {
            currentIndex--
            setCurrentSong(audioList[currentIndex])
        }
    }

    private fun playAudio(audio: Audio) {
        musicService?.play(audio.path) {
            playNext() // Auto-play next song when current song completes
        }
        _isPlaying.postValue(true)
    }

    fun pauseAudio() {
        musicService?.pauseAudio()
        _isPlaying.postValue(false)
    }

    fun resumeAudio() {
        musicService?.resumeAudio()
        _isPlaying.postValue(true)
    }

    fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    fun startSeekBarUpdates() {
        handler.post(updateSeekRunnable)
    }

    fun stopSeekBarUpdates() {
        handler.removeCallbacks(updateSeekRunnable)
    }

    fun bindService(activity: Activity) {
        if (!serviceBound) {
            val intent = Intent(activity, MusicService::class.java)
            activity.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(activity: Activity) {
        if (serviceBound) {
            activity.unbindService(musicServiceConnection)
            serviceBound = false
        }
    }

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as MusicService.MusicBinder
            musicService = musicBinder.getService()
            serviceBound = true
            Log.d("PlayerViewModel", "Service connected")
            _currentAudioTrack.value?.let { audio -> playAudio(audio) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            serviceBound = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSeekBarUpdates()
        // Optionally: Release resources like musicService
    }
}