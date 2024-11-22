// MapsViewModel.kt
package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase
) : ViewModel() {

    private val _weatherData = MutableStateFlow<Resource<List<LocationResponse>>>(Resource.Loading())
    val weatherData: StateFlow<Resource<List<LocationResponse>>> = _weatherData.asStateFlow()

    private val weatherCache = mutableMapOf<String, LocationResponse>()

    fun getWeatherByIdentifier(identifier: String) {
        val cachedWeather = weatherCache[identifier]
        if (cachedWeather != null) {
            _weatherData.value = Resource.Success(listOf(cachedWeather))
            return
        }

        viewModelScope.launch {
            getWeatherByLocationsUseCase(listOf(identifier) )
                .onStart { _weatherData.value = Resource.Loading() }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _weatherData.value = Resource.Loading()
                        }
                        is Resource.Success -> {
                            _weatherData.value = if (result.data.isNullOrEmpty()) {
                                Resource.Error("Data not found")
                            } else {
                                Resource.Success(result.data)
                            }
                            weatherCache[identifier] = if (result.data.isNullOrEmpty()) {
                                LocationResponse()
                            } else {
                                result.data[0]
                            }
                        }
                        is Resource.Error -> {
                            _weatherData.value = Resource.Error(result.message ?: "An unexpected error occurred")
                        }
                    }
                }
        }
    }

    fun clearWeatherData() {
        _weatherData.value = Resource.Loading()
    }
}
