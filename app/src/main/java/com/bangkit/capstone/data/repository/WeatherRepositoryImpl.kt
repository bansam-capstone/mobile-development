package com.bangkit.capstone.data.repository

import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.data.remote.retrofit.ApiService
import com.bangkit.capstone.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository {

    override suspend fun getWeather(): WeatherResponse {
        return apiService.getWeather()
    }

    override suspend fun getWeatherByLocation(location: String): LocationResponse {
        return apiService.getWeatherByLocation(location)
    }

}