package com.bangkit.capstone.presentation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.databinding.ActivityHomeBinding
import com.bangkit.capstone.domain.model.FloodCategories
import com.bangkit.capstone.presentation.view.help.HelpActivity
import com.bangkit.capstone.presentation.view.maps.MapsActivity
import com.bangkit.capstone.presentation.view.mitigation.MitigationActivity
import com.bangkit.capstone.presentation.view.prediction.PredictionActivity
import com.bangkit.capstone.presentation.view.settings.SettingsActivity
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityHomeBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var mMap: GoogleMap

    private val initialLocation = LatLng(-0.502106, 117.153709)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyFadeOutEffect(R.id.scrollView, R.id.cardLocation, 500f)
        applyFadeOutEffect(R.id.scrollView, R.id.cardWeather, 1500f)
        applyFadeOutEffect(R.id.scrollView, R.id.cardWeatherInfo, 1500f)
        applyFadeOutEffect(R.id.scrollView, R.id.cardFloodPrediction, 1500f)
        applyFadeInEffect(R.id.scrollView, R.id.cardMaps, 1500f)
        applyFadeInEffect(R.id.scrollView, R.id.cardTipsMitigation, 500f)

        setupUI()

        updateBackgroundBasedOnTheme()

        setupWeatherInfo()

        getDataPredictionData()

        binding.btnLihatTitikLokasi.setOnClickListener {
            val intent = Intent(this, PredictionActivity::class.java)
            startActivity(intent)
        }

        binding.btnLihatMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLihatTipsMitigation.setOnClickListener {
            val intent = Intent(this, MitigationActivity::class.java)
            startActivity(intent)
        }

        binding.mapView.getMapAsync(this)
        binding.mapView.onCreate(savedInstanceState)

    }

    private fun applyFadeOutEffect(scrollViewId: Int, targetViewId: Int, maxFadeDistance: Float) {
        val scrollView = findViewById<ScrollView>(scrollViewId)
        val targetView = findViewById<View>(targetViewId)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            val alphaValue = 1 - (scrollY / maxFadeDistance)
            targetView.alpha = alphaValue.coerceIn(0f, 1f)
        }
    }

    private fun applyFadeInEffect(scrollViewId: Int, targetViewId: Int, maxFadeDistance: Float) {
        val scrollView = findViewById<ScrollView>(scrollViewId)
        val targetView = findViewById<View>(targetViewId)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            val alphaValue = (scrollY / maxFadeDistance).coerceIn(0f, 1f)
            targetView.alpha = alphaValue
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))

        mMap.setOnMapClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateFloodCategories(weatherList: List<LocationResponse>): FloodCategories {
        var aman = 0
        var waspada = 0
        var bahaya = 0

        weatherList.forEach { location ->
            when (location.riskLevel) {
                "Aman" -> aman++
                "Waspada" -> waspada++
                "Bahaya" -> bahaya++
            }
        }

        return FloodCategories(aman, waspada, bahaya)
    }

    private fun setupUI(){
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupWeatherInfo() {
        weatherViewModel.weather.observe(this) { weather ->
            if (weather.isNotEmpty()) {
                val currentWeather = weather[0]
                binding.tvLocation.text = currentWeather.city ?: "Lokasi tidak tersedia"
                binding.tvDate.text = getFormattedLocalDateTime()

                binding.tvTemperature.text = "${currentWeather.temperature?.toInt() ?: "--"}°C"
                binding.tvWeatherDescription.text = descriptionToIndonesian(currentWeather.description ?: "Tidak diketahui")
                binding.tvWindSpeed.text = String.format("%s m/s", currentWeather.windSpeed?.toString() ?: "--")
                binding.tvHumidity.text = String.format("%s%%", currentWeather.humidity?.toInt() ?: "--")
                binding.tvPressure.text = String.format("%d hPa", currentWeather.pressure?.toInt() ?: "--")

                currentWeather.description?.let { updateWeatherIcon(it) }
            } else {
                Toast.makeText(this, "Data cuaca tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDataPredictionData(){
        weatherViewModel.getWeatherByLocation(listOf("slamet-riyadi", "antasari", "simpang-agus-salim", "mugirejo", "simpang-lembuswana",
            "kapten-sudjono", "brigjend-katamso", "gatot-subroto", "cendana", "di-panjaitan",
            "damanhuri", "pertigaan-pramuka-perjuangan", "padat-karya-sempaja-simpang-wanyi",
            "simpang-sempaja", "ir-h-juanda", "tengkawang", "sukorejo"))

        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.pbWeatherPredictionLoading.visibility = View.VISIBLE

                        binding.tvFloodPredictionAntasari.visibility = View.GONE
                        binding.tvFloodPredictionSlametRiyadi.visibility = View.GONE
                        binding.ivFloodPredictionAntasari.visibility = View.GONE
                        binding.ivFloodPredictionSlametRiyadi.visibility = View.GONE
                        binding.llAntasari.visibility = View.GONE
                        binding.llSlametRiyadi.visibility = View.GONE
                        binding.tvFloodPredictionAntasariTitle.visibility = View.GONE
                        binding.tvFloodPredictionSlametRiyadiTitle.visibility = View.GONE

                    }
                    is Resource.Success -> {
                        binding.pbWeatherPredictionLoading.visibility = View.GONE

                        binding.tvFloodPredictionAntasari.visibility = View.VISIBLE
                        binding.tvFloodPredictionSlametRiyadi.visibility = View.VISIBLE
                        binding.ivFloodPredictionAntasari.visibility = View.VISIBLE
                        binding.ivFloodPredictionSlametRiyadi.visibility = View.VISIBLE
                        binding.llAntasari.visibility = View.VISIBLE
                        binding.llSlametRiyadi.visibility = View.VISIBLE
                        binding.tvFloodPredictionAntasariTitle.visibility = View.VISIBLE
                        binding.tvFloodPredictionSlametRiyadiTitle.visibility = View.VISIBLE
                        val weatherList = resource.data
                        weatherList?.forEach { locationWeather ->
                            locationWeather.location?.let {
                                when (it) {
                                    "slamet-riyadi" -> {
                                       binding.tvFloodPredictionSlametRiyadi.text = locationWeather.riskLevel ?: "Tidak diketahui"
                                        if (locationWeather.riskLevel == "Aman") {
                                            binding.tvFloodPredictionSlametRiyadi.setTextColor(resources.getColor(R.color.success_900))
                                            binding.ivFloodPredictionSlametRiyadi.setImageResource(R.drawable.ic_prediction_safe)
                                            binding.llSlametRiyadi.setBackgroundResource(R.drawable.bg_status_safe)
                                        } else if (locationWeather.riskLevel == "Waspada") {
                                            binding.tvFloodPredictionSlametRiyadi.setTextColor(resources.getColor(R.color.warning_900))
                                            binding.ivFloodPredictionSlametRiyadi.setImageResource(R.drawable.ic_prediction_warning)
                                            binding.llSlametRiyadi.setBackgroundResource(R.drawable.bg_status_warning)
                                        } else if (locationWeather.riskLevel == "Bahaya") {
                                            binding.tvFloodPredictionSlametRiyadi.setTextColor(resources.getColor(R.color.danger_900))
                                            binding.ivFloodPredictionSlametRiyadi.setImageResource(R.drawable.ic_prediction_danger)
                                            binding.llSlametRiyadi.setBackgroundResource(R.drawable.bg_status_danger)
                                        } else {
                                            binding.tvFloodPredictionSlametRiyadi.setTextColor(resources.getColor(R.color.black))
                                        }
                                    }
                                    "antasari" -> {
                                        binding.tvFloodPredictionAntasari.text = locationWeather.riskLevel ?: "Tidak diketahui"
                                        if (locationWeather.riskLevel == "Aman") {
                                            binding.tvFloodPredictionAntasari.setTextColor(resources.getColor(R.color.success_900))
                                            binding.ivFloodPredictionAntasari.setImageResource(R.drawable.ic_prediction_safe)
                                            binding.llAntasari.setBackgroundResource(R.drawable.bg_status_safe)
                                        } else if (locationWeather.riskLevel == "Waspada") {
                                            binding.tvFloodPredictionAntasari.setTextColor(resources.getColor(R.color.warning_900))
                                            binding.ivFloodPredictionAntasari.setImageResource(R.drawable.ic_prediction_warning)
                                            binding.llAntasari.setBackgroundResource(R.drawable.bg_status_warning)
                                        } else if (locationWeather.riskLevel == "Bahaya") {
                                            binding.tvFloodPredictionAntasari.setTextColor(resources.getColor(R.color.danger_900))
                                            binding.ivFloodPredictionAntasari.setImageResource(R.drawable.ic_prediction_danger)
                                            binding.llAntasari.setBackgroundResource(R.drawable.bg_status_danger)
                                        } else {
                                            binding.tvFloodPredictionAntasari.setTextColor(resources.getColor(R.color.black))
                                        }
                                    }
                                }
                                val categories = calculateFloodCategories(weatherList)
                                updateFloodPredictionTotals(categories)
                            }
                        }
                    }
                    is Resource.Error -> {
                        binding.pbWeatherPredictionLoading.visibility = View.GONE
                        Toast.makeText(this@HomeActivity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateFloodPredictionTotals(categories: FloodCategories) {
        binding.tvSafeStatus.text = categories.aman.toString()
        binding.tvWarningStatus.text = categories.waspada.toString()
        binding.tvDangerStatus.text = categories.bahaya.toString()
    }


    private fun descriptionToIndonesian(description: String): String {
        return when (description) {
            "clear sky" -> "Cerah"
            "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> "Berawan"
            "shower rain", "rain", "light rain", "moderate rain", "very heavy rain", "extreme rain" -> "Hujan"
            "thunderstorm", "thunderstorm with light rain", "thunderstorm with heavy rain" -> "Badai Petir"
            else -> "Tidak diketahui"
        }
    }

    private fun updateWeatherIcon(weatherIcon: String) {
        val currentHour = LocalTime.now().hour

        val isDaytime = currentHour in 6..18

        when (weatherIcon) {
            "clear sky" -> {
                if (isDaytime) {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_day_clearsky)
                    binding.animationWeatherIcon.playAnimation()
                } else {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_night_clearsky)
                    binding.animationWeatherIcon.playAnimation()
                }
            }
            "few clouds", "scattered clouds" -> {
                if (isDaytime) {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_day_clouds)
                    binding.animationWeatherIcon.playAnimation()
                } else {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_night_cloud)
                    binding.animationWeatherIcon.playAnimation()
                }
            }
            "broken clouds", "overcast clouds" -> {
                if (isDaytime) {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_day_clouds)
                    binding.animationWeatherIcon.playAnimation()
                } else {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_night_cloud)
                    binding.animationWeatherIcon.playAnimation()
                }
            }
            "shower rain", "rain", "light rain", "moderate rain", "very heavy rain", "extreme rain" -> {
                if (isDaytime) {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_day_rain)
                    binding.animationWeatherIcon.playAnimation()
                    binding.animationView.visibility = View.VISIBLE
                } else {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_night_rain)
                    binding.animationWeatherIcon.playAnimation()
                    binding.animationView.visibility = View.VISIBLE
                }
            }
            "thunderstorm", "thunderstorm with light rain", "thunderstorm with heavy rain" -> {
                if (isDaytime) {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_day_tstorms)
                    binding.animationWeatherIcon.playAnimation()
                    binding.animationView.visibility = View.VISIBLE
                } else {
                    binding.animationWeatherIcon.setAnimation(R.raw.weather_night_tstorms)
                    binding.animationWeatherIcon.playAnimation()
                    binding.animationView.visibility = View.VISIBLE
                }
            }
            else -> {
                binding.animationWeatherIcon.setAnimation(R.raw.weather_day_clouds)
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
                binding.mainContainer.setBackgroundResource(R.drawable.bg_gradient_dark)
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
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
            R.id.action_help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
