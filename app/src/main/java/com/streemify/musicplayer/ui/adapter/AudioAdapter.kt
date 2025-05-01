package com.streemify.musicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.streemify.musicplayer.R
import com.streemify.musicplayer.ui.data.Audio
import com.streemify.musicplayer.ui.utils.ThumbnailLoader

class AudioAdapter(
    private val audioList: List<Audio>,
    private val onItemClick: (Audio) -> Unit
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.audio_title)  // Corrected ID for title
        val artistText: TextView = itemView.findViewById(R.id.audio_artist)
        val thumbnailImage: ImageView = itemView.findViewById(R.id.audio_thumbnail)

        fun bind(audio: Audio) {
            titleText.text = audio.title
            artistText.text = audio.artist

            val thumbnail = ThumbnailLoader.getThumbnail(itemView.context, audio.path)
            if (thumbnail != null) {
                thumbnailImage.setImageBitmap(thumbnail)
            } else {
                thumbnailImage.setImageResource(R.drawable.ic_music_note) // Default thumbnail
            }

            itemView.setOnClickListener {
                onItemClick(audio)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(audioList[position])
    }

    override fun getItemCount(): Int = audioList.size
}