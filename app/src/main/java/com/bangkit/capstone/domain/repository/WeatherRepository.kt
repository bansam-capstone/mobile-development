package com.bangkit.capstone.domain.repository

import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(): WeatherResponse

    suspend fun getWeatherByLocation(location: String): LocationResponse
}