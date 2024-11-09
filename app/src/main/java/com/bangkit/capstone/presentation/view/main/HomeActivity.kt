package com.bangkit.capstone.presentation.view.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
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
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateBackgroundBasedOnTheme()

        setupWeatherInfo()

        binding.btnLihatTitikLokasi.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupWeatherInfo() {
        weatherViewModel.weather.observe(this) { weather ->
            if (weather.isNotEmpty()) {
                val currentWeather = weather[0]
                binding.tvLocation.text = currentWeather.city
                binding.tvDate.text = getFormattedLocalDateTime()
                binding.tvTemperature.text = currentWeather.temperature.toString() + "°C"
                binding.tvWeatherDescription.text = descriptionToIndonesian(currentWeather.description!!)
                binding.tvWindSpeed.text = currentWeather.windSpeed.toString() + " m/s"
                binding.tvHumidity.text = currentWeather.humidity.toString() + "%"
                binding.tvPressure.text = currentWeather.pressure.toString()

                currentWeather.description?.let { updateWeatherIcon(it) }
            }
        }
    }

    private fun descriptionToIndonesian(description: String): String {
        return when (description) {
            "clear sky" -> "Cerah"
            "few clouds" -> "Berawan"
            "scattered clouds" -> "Berawan"
            "broken clouds" -> "Berawan"
            "shower rain" -> "Hujan"
            "rain" -> "Hujan"
            "thunderstorm" -> "Badai Petir"
            else -> "Cerah"
        }
    }

    private fun updateWeatherIcon(weatherIcon: String) {
        val currentHour = LocalTime.now().hour

        val isDaytime = currentHour in 6..18

        when (weatherIcon) {
            "clear sky" -> {
                if (isDaytime) {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_day_clearsky)
                } else {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_night_clearsky)
                }
            }
            "few clouds" -> {
                if (isDaytime) {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_day_fewclouds)
                } else {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_night_fewclouds)
                }
            }
            "scattered clouds", "broken clouds" -> {
                if (isDaytime) {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_day_brokenclouds)
                } else {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_night_brokenclouds)
                }
            }
            "shower rain", "rain" -> {
                if (isDaytime) {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_daynight_rain)
                } else {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_daynight_rain)
                }
            }
            "thunderstorm" -> {
                if (isDaytime) {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_daynight_tstorms)
                } else {
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_daynight_tstorms)
                }
            }
            else -> {
                binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_day_fewclouds)
            }
        }
    }

    private fun getFormattedLocalDateTime(): String {
        val current = LocalDateTime.now()

        val dayOfWeek = current.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = current.format(formatter)

        return "$dayOfWeek, $formattedDate"
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
