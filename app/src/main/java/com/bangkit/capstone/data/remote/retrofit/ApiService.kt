package com.bangkit.capstone.data.remote.retrofit

import com.bangkit.capstone.data.remote.response.WeatherResponseItem
import retrofit2.http.GET

interface ApiService {

    @GET("weather")
    suspend fun getWeather(): List<WeatherResponseItem>
}