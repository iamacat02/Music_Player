package com.streemify.musicplayer.ui.screen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.streemify.musicplayer.MainActivity
import com.streemify.musicplayer.R
import com.streemify.musicplayer.databinding.ActivityLibraryBinding
import kotlin.jvm.java

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(resources.getColor(R.color.background, null)),
            navigationBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            )
        )
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.library)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl("https://www.youtube.com/@T-TouchOfficial/videos") // replace with real ID
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
        }

        binding.bottomNavigation.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.bottomNavigation.navPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        binding.include.materialToolbar.title = "Library"
    }
}