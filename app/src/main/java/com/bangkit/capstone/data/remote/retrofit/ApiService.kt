package com.bangkit.capstone.data.remote.retrofit

import com.bangkit.capstone.BuildConfig
import com.bangkit.capstone.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double = -0.494823,
        @Query("lon") lon: Double = 117.143616,
        @Query("appid") apiKey: String = BuildConfig.API_WEATHER
    ): WeatherResponse
}