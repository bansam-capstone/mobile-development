package com.bangkit.capstone.presentation.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        upadateLogo()

        val splashDuration = 1500L

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, splashDuration)
    }

    private fun upadateLogo() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                binding.logoImage.setImageResource(R.drawable.ic_logo_bansam_dark)
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                binding.logoImage.setImageResource(R.drawable.ic_logo_bansam)
            }
        }
    }
}