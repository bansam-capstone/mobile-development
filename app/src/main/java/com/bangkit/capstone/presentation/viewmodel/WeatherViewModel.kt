// app/src/main/java/com/bangkit/capstone/presentation/viewmodel/WeatherViewModel.kt
package com.bangkit.capstone.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.domain.use_case.get_weather.GetWeatherUseCase
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import com.bangkit.capstone.util.WeatherWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    private val _weather = MutableLiveData<List<WeatherResponse>>()
    val weather: LiveData<List<WeatherResponse>> = _weather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _weatherData = MutableStateFlow<Resource<List<LocationResponse>>>(Resource.Loading())
    val weatherData: StateFlow<Resource<List<LocationResponse>>> = _weatherData

    init {
        getWeather()
    }

    private fun getWeather() {
        viewModelScope.launch {
            getWeatherUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _weather.value = result.data ?: emptyList()
                        _error.value = null
                        _isLoading.value = false

                        result.data?.firstOrNull()?.let { weather ->
                            sharedPreferences.edit().apply {
                                putString("temperature", weather.temperature.toString())
                                putString("weather_condition", weather.description)
                                putString("weather_prediction", weather.riskLevel ?: "N/A")
                                apply()
                            }

                            updateWidget()
                        }
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun getWeatherByLocation(locations: List<String>) {
        viewModelScope.launch {
            getWeatherByLocationsUseCase(locations).collect { result ->
                _weatherData.value = result
            }
        }
    }

    private fun updateWidget() {
        val context = getApplication<Application>().applicationContext
        val intent = android.content.Intent(context, WeatherWidget::class.java).apply {
            action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = android.appwidget.AppWidgetManager.getInstance(context)
                .getAppWidgetIds(android.content.ComponentName(context, WeatherWidget::class.java))
            putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
