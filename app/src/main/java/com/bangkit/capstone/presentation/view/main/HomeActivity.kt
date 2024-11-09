package com.bangkit.capstone.presentation.view.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityHomeBinding
import com.bangkit.capstone.presentation.view.maps.MapsActivity
import com.bangkit.capstone.presentation.view.mitigation.MitigationActivity
import com.bangkit.capstone.presentation.view.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateBackgroundBasedOnTheme()

        binding.btnLihatTitikLokasi.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.btnSeeAll.setOnClickListener {
            val intent = Intent(this, MitigationActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateBackgroundBasedOnTheme() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                binding.cardWeatherInfo.setBackgroundResource(R.drawable.bg_card_dark)
                binding.cardFloodPrediction.setBackgroundResource(R.drawable.bg_card_dark)
                binding.mainContainer.setBackgroundResource(R.drawable.bg_gradient_dark)
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                binding.cardWeatherInfo.setBackgroundResource(R.drawable.bg_card_light)
                binding.cardFloodPrediction.setBackgroundResource(R.drawable.bg_card_light)
                binding.mainContainer.setBackgroundResource(R.drawable.bg_gradient_light)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
