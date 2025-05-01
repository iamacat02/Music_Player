package com.streemify.musicplayer

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import com.streemify.musicplayer.databinding.ActivityMainBinding
import com.streemify.musicplayer.ui.adapter.AudioAdapter
import com.streemify.musicplayer.ui.data.Audio
import com.streemify.musicplayer.ui.model.PlayerViewModel
import com.streemify.musicplayer.ui.model.PlayerViewModelProvider
import com.streemify.musicplayer.ui.screen.LibraryActivity
import com.streemify.musicplayer.ui.screen.PlayerActivity
import com.streemify.musicplayer.ui.sharedpref.SaveData
import com.streemify.musicplayer.ui.utils.AudioLoader
import com.streemify.musicplayer.ui.utils.ThumbnailLoader.getThumbnail

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var  playerViewModel: PlayerViewModel

    private var audioList = mutableListOf<Audio>()
    private lateinit var adapter: AudioAdapter
    private val saveData: SaveData = SaveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge UI
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = resources.getColor(R.color.background, null)
            ),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets for padding adjustment
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playerViewModel = PlayerViewModelProvider.getInstance(application)

        // Load saved music details
        saveData.getSavedMusicDetails(this)?.apply {
            binding.homeTittle.text = this.title
            binding.homeArtist.text = this.artist
            binding.homeThumbnail.setImageBitmap(getThumbnail(this@MainActivity, this.path.toString()))
        }

        // Request permissions
        requestPermissions()

        // Set up the audio list
        loadAudioFiles()

        // Set up the adapter after audio files are loaded
        adapter = AudioAdapter(audioList, onItemClick = { audio ->
            saveData.saveMusicDetails(this, audio) // Save song details
            playerViewModel.setAudioList(audioList)
            playerViewModel.setCurrentSong(audio)
            startActivity(Intent(this, PlayerActivity::class.java))
        })

        binding.recycleView.adapter = adapter

        // Handle Play/Pause button interaction
        playerViewModel.isPlaying.observe(this) { isPlaying ->
            binding.homePlayPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)

            binding.homeTittle.text = playerViewModel.currentAudioTrack.value?.title ?: ""
            binding.homeArtist.text = playerViewModel.currentAudioTrack.value?.artist ?: ""

            binding.homePlayPause.setOnClickListener {
                if (isPlaying) {
                    playerViewModel.pauseAudio()
                } else {
                    playerViewModel.resumeAudio()
                }
            }
        }

        // Observe and update thumbnail
        playerViewModel.thumbnail.observe(this) { bitmap ->
            binding.homeThumbnail.setImageBitmap(bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.ic_music_note))
        }

        //Previous Button
        binding.homePrevious.setOnClickListener {
            playerViewModel.playPrevious()
        }

        //Next Button
        binding.homeNext.setOnClickListener {
            playerViewModel.playNext()
        }

        findViewById<AppCompatImageButton>(R.id.nav_player).setOnClickListener {
            if (playerViewModel.isPlaying.value == true) {
                startActivity(Intent(this, PlayerActivity::class.java))
                Log.d("MainActivity", "Player button clicked")
            } else {
                Toast.makeText(this, "No song is playing", Toast.LENGTH_SHORT).show()
            }

        }

        findViewById<AppCompatImageButton>(R.id.nav_library).setOnClickListener {
       startActivity(Intent(this, LibraryActivity::class.java))
            Log.d("MainActivity", "LibraryActivity button clicked")
        }

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX.init(this).permissions(
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.POST_NOTIFICATIONS
            ).request { _, grantedList, _ ->
                if (android.Manifest.permission.READ_MEDIA_AUDIO in grantedList) {
                    loadAudioFiles()
                }
            }
        } else {
            PermissionX.init(this).permissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).request { _, grantedList, _ ->
                if (android.Manifest.permission.READ_EXTERNAL_STORAGE in grantedList) {
                    loadAudioFiles()
                }
            }
        }
    }

    private fun loadAudioFiles() {
        // Load audio files after permissions are granted
        audioList = AudioLoader.loadAudio(this) as MutableList<Audio>
    }

    override fun onStart() {
        super.onStart()
        binding.include.materialToolbar.title = "Music Player"
        playerViewModel.bindService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}