package com.bangkit.capstone.data.repository

import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.data.remote.response.WeatherResponseItem
import com.bangkit.capstone.data.remote.retrofit.ApiService
import com.bangkit.capstone.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository{

    override suspend fun getWeather(): List<WeatherResponseItem> {
        return apiService.getWeather()
    }

}