package com.streemify.musicplayer.ui.utils

import android.content.Context
import android.provider.MediaStore
import com.streemify.musicplayer.ui.data.Audio

object AudioLoader {
    fun loadAudio(context: Context): List<Audio> {
        val audioList = mutableListOf<Audio>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

                val thumbnail = ThumbnailLoader.getThumbnail(context, path)

                val audio = Audio(title, artist, path, duration,
                    thumbnail
                )
                audioList.add(audio)

            } while (cursor.moveToNext())

            cursor.close()
        }

        return audioList
    }
}