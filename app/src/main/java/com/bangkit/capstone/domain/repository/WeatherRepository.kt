package com.bangkit.capstone.domain.repository

import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.data.remote.response.WeatherResponseItem

interface WeatherRepository {
    suspend fun getWeather(): List<WeatherResponseItem>
}