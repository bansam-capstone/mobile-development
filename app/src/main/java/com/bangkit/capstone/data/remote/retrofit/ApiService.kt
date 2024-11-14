package com.bangkit.capstone.data.remote.retrofit

import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("samarinda")
    suspend fun getWeather(): WeatherResponse

    @GET("{location}")
    suspend fun getWeatherByLocation(@Path("location") location: String): LocationResponse

}