package com.bangkit.capstone.presentation.view.settings

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.databinding.ActivitySettingsBinding
import com.bangkit.capstone.domain.model.FloodCategories
import com.bangkit.capstone.presentation.view.about.AboutActivity
import com.bangkit.capstone.presentation.view.termsandpolicy.TermsPolicyActivity
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import com.bangkit.capstone.util.FloodReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//
//        // Setup switch notifikasi
//        setupNotificationSwitch()

        binding.llAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.llTerms.setOnClickListener {
            val intent = Intent(this, TermsPolicyActivity::class.java)
            intent.putExtra(TermsPolicyActivity.EXTRA_FRAGMENT_TYPE, TermsPolicyActivity.FRAGMENT_TERMS)
            startActivity(intent)
        }

        binding.llPrivacy.setOnClickListener {
            val intent = Intent(this, TermsPolicyActivity::class.java)
            intent.putExtra(TermsPolicyActivity.EXTRA_FRAGMENT_TYPE, TermsPolicyActivity.FRAGMENT_POLICY)
            startActivity(intent)
        }
    }

    private fun setupNotificationSwitch(){
        val switchNotification = binding.swNotification
        switchNotification.isChecked = sharedPreferences.getBoolean("notification", true)

        switchNotification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked){
                scheduleFloodReminderWorker()
                sharedPreferences.edit().putBoolean("notification", true).apply()
            } else {
                cancelFloodReminderWorker()
                sharedPreferences.edit().putBoolean("notification", false).apply()
            }
        }
    }

    private fun scheduleFloodReminderWorker(){
        val uniqueWorkName = "FloodReminderWork"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val floodReminderWorkRequest = PeriodicWorkRequestBuilder<FloodReminderWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.REPLACE,
            floodReminderWorkRequest
        )
    }

    private fun cancelFloodReminderWorker(){
        val uniqueWorkName = "FloodReminderWork"
        WorkManager.getInstance(this).cancelUniqueWork(uniqueWorkName)
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

    private fun getDataPrediction(){
        weatherViewModel.getWeatherByLocation(listOf(
            "slamet-riyadi", "antasari", "simpang-agus-salim", "mugirejo",
            "simpang-lembuswana", "kapten-sudjono", "brigjend-katamso",
            "gatot-subroto", "cendana", "di-panjaitan", "damanhuri",
            "pertigaan-pramuka-perjuangan", "padat-karya-sempaja-simpang-wanyi",
            "simpang-sempaja", "ir-h-juanda", "tengkawang", "sukorejo"
        ))

        lifecycleScope.launch {
            weatherViewModel.weatherData.collect{ resources ->
                when(resources){
                    is Resource.Loading -> {
                        // Handle loading
                    }
                    is Resource.Success -> {
                        val weatherList = resources.data
                        weatherList?.forEach{ locationWeather ->
                            locationWeather.location?.let {
                                calculateFloodCategories(weatherList)
                            }
                        }
                    }
                    is Resource.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
