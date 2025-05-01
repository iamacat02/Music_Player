package com.streemify.musicplayer.ui.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audio(
    val title: String,
    val artist: String,
    val path: String,
    val duration: Long,
    val thumbnail: Bitmap?
): Parcelable