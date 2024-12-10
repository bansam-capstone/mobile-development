package com.bangkit.capstone.presentation.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
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

        val splashDuration = 2500L

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, splashDuration)

        startAnimation()
    }

    private fun startAnimation() {
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logoImage.startAnimation(fadeIn)
        binding.appName.startAnimation(fadeIn)
        binding.appTagline.startAnimation(fadeIn)
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