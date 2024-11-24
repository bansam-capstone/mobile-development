package com.bangkit.capstone.domain.use_case.get_weathertommorow

import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetWeatherTomorrowByLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(locations: List<String>): Flow<Resource<List<LocationResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val weatherData = locations.map { location ->
                repository.getWeatherTommorowByLocation(location)
            }
            emit(Resource.Success(weatherData))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

}