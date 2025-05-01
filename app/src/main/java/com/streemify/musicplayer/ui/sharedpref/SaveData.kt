package com.streemify.musicplayer.ui.sharedpref

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.streemify.musicplayer.ui.data.Audio
import androidx.core.content.edit
import java.io.ByteArrayOutputStream

class SaveData {

    fun saveMusicDetails(context: Context, audio: Audio) {
        val musicPreferences = context.getSharedPreferences("music", Context.MODE_PRIVATE)
        musicPreferences.edit {
            putString("title", audio.title)
            putString("artist", audio.artist)
            putString("path", audio.path)
            putLong("duration", audio.duration)
        }
    }

    fun getSavedMusicDetails(context: Context): Audio? {
        val musicPreferences = context.getSharedPreferences("music", Context.MODE_PRIVATE)

        val title = musicPreferences.getString("title", null)
        val artist = musicPreferences.getString("artist", null)
        val path = musicPreferences.getString("path", null)
        val duration = musicPreferences.getLong("duration", -1L)
        val thumbnailBase64 = musicPreferences.getString("thumbnailBitmap", null)

        return if (title != null && artist != null && path != null && duration != -1L) {
            val thumbnail = thumbnailBase64?.let { base64ToBitmap(it) }
            Audio(
                title = title,
                artist = artist,
                path = path,
                duration = duration,
                thumbnail = thumbnail
            )
        } else {
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
