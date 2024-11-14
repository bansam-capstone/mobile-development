package com.bangkit.capstone.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.domain.use_case.get_weather.GetWeatherUseCase
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase
) : ViewModel() {
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
}
