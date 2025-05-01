package com.streemify.musicplayer.ui.screen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.streemify.musicplayer.MainActivity
import com.streemify.musicplayer.R
import com.streemify.musicplayer.databinding.ActivityPlayerBinding
import com.streemify.musicplayer.ui.model.PlayerViewModel
import com.streemify.musicplayer.ui.model.PlayerViewModelProvider
import com.streemify.musicplayer.ui.utils.DurationConverter

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = resources.getColor(
                    R.color.background, null
                )
            ), navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT)
        )
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set padding for the system bars (status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playerViewModel = PlayerViewModelProvider.getInstance(application)

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying == true) {
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
            } else {
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
            }
        }

        playerViewModel.currentAudioTrack.observe(this) { audio ->
            audio?.let {
                binding.titlePlayer.text = it.title
                binding.tittlePlayerDesc.text = it.artist
                binding.thumbnailPlayer.setImageBitmap(it.thumbnail)
                binding.maxDuration.text = DurationConverter.convertDuration(audio.duration.toInt())
                binding.appCompatSeekBar.max = audio.duration.toInt()
            }
        }

        playerViewModel.currentPosition.observe(this) { position ->
            binding.appCompatSeekBar.progress = position
            binding.startDuration.text = DurationConverter.convertDuration(position)
        }

        binding.previousButton.setOnClickListener {
            playerViewModel.playPrevious()

        }
        binding.playPauseButton.setOnClickListener {
            if (playerViewModel.isPlaying.value == true) {
                playerViewModel.pauseAudio()
            } else {
                playerViewModel.resumeAudio()
            }
        }

        binding.nextButton.setOnClickListener {
            playerViewModel.playNext()
        }

        binding.appCompatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean
            ) {
                if (p2) {
                    playerViewModel.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        findViewById<AppCompatImageButton>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<AppCompatImageButton>(R.id.nav_library).setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.startSeekBarUpdates()
        binding.include.materialToolbar.apply {
            title = "Now Playing"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                startActivity(Intent(this@PlayerActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        playerViewModel.stopSeekBarUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
