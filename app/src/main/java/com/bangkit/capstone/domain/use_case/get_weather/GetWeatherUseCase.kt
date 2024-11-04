package com.bangkit.capstone.domain.use_case.get_weather

import android.util.Log
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(): Flow<Resource<WeatherResponse>> = flow {
        try {
            emit(Resource.Loading())
            val weatherData = repository.getWeather()
            emit(Resource.Success(weatherData))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            Log.e("GetWeatherUseCase", "Network error: ${e.message}")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
