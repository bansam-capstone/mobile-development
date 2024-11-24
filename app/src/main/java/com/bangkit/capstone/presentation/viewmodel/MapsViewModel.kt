// MapsViewModel.kt
package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import com.bangkit.capstone.domain.use_case.get_weathertommorow.GetWeatherTomorrowByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase,
    private val getWeatherTomorrowByLocationUseCase: GetWeatherTomorrowByLocationUseCase
) : ViewModel() {

    private val _weatherTodayData = MutableStateFlow<Resource<List<LocationResponse>>>(Resource.Loading())
    val weatherTodayData: StateFlow<Resource<List<LocationResponse>>> = _weatherTodayData.asStateFlow()

    private val _weatherTommorowdData = MutableStateFlow<Resource<List<LocationResponse>>>(Resource.Loading())
    val weatherTommorowData: StateFlow<Resource<List<LocationResponse>>> = _weatherTommorowdData.asStateFlow()

    private val weatherTodayCache = mutableMapOf<String, LocationResponse>()
    private val weatherTommorowCache = mutableMapOf<String, LocationResponse>()

    fun getWeatherTodayByIdentifier(identifier: String) {
        val cachedWeather = weatherTodayCache[identifier]
        if (cachedWeather != null) {
            _weatherTodayData.value = Resource.Success(listOf(cachedWeather))
            return
        }

        viewModelScope.launch {
            getWeatherByLocationsUseCase(listOf(identifier) )
                .onStart { _weatherTodayData.value = Resource.Loading() }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _weatherTodayData.value = Resource.Loading()
                        }
                        is Resource.Success -> {
                            _weatherTodayData.value = if (result.data.isNullOrEmpty()) {
                                Resource.Error("Data not found")
                            } else {
                                Resource.Success(result.data)
                            }
                            weatherTodayCache[identifier] = if (result.data.isNullOrEmpty()) {
                                LocationResponse()
                            } else {
                                result.data[0]
                            }
                        }
                        is Resource.Error -> {
                            _weatherTodayData.value = Resource.Error(result.message ?: "An unexpected error occurred")
                        }
                    }
                }
        }
    }

    fun getWeatherTommorowByIdentifier(identifier: String) {
        val cachedWeather = weatherTommorowCache[identifier]
        if (cachedWeather != null) {
            _weatherTommorowdData.value = Resource.Success(listOf(cachedWeather))
            return
        }

        viewModelScope.launch {
            getWeatherTomorrowByLocationUseCase(listOf(identifier) )
                .onStart { _weatherTommorowdData.value = Resource.Loading() }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _weatherTommorowdData.value = Resource.Loading()
                        }
                        is Resource.Success -> {
                            _weatherTommorowdData.value = if (result.data.isNullOrEmpty()) {
                                Resource.Error("Data not found")
                            } else {
                                Resource.Success(result.data)
                            }
                            weatherTommorowCache[identifier] = if (result.data.isNullOrEmpty()) {
                                LocationResponse()
                            } else {
                                result.data[0]
                            }
                        }
                        is Resource.Error -> {
                            _weatherTodayData.value = Resource.Error(result.message ?: "An unexpected error occurred")
                        }
                    }
                }
        }
    }

    fun clearWeatherData() {
        _weatherTodayData.value = Resource.Loading()
    }
}
