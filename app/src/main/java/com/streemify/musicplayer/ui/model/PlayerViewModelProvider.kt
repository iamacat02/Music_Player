package com.streemify.musicplayer.ui.model

import android.app.Application

object PlayerViewModelProvider {
    private var instance: PlayerViewModel? = null
    fun getInstance(application: Application): PlayerViewModel {
        if (instance == null) {
            instance = PlayerViewModel(application)
        }
        return instance!!
    }
}