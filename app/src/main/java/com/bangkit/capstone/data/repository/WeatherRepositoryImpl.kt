package com.bangkit.capstone.data.repository

import com.bangkit.capstone.data.local.room.LocationWeatherDao
import com.bangkit.capstone.data.local.room.WeatherDao
import com.bangkit.capstone.data.mapper.toDomain
import com.bangkit.capstone.data.mapper.toEntity
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.data.remote.response.WeatherResponse
import com.bangkit.capstone.data.remote.retrofit.ApiService
import com.bangkit.capstone.domain.repository.WeatherRepository
import java.time.ZoneId
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val weatherDao: WeatherDao,
    private val locationWeatherDao: LocationWeatherDao
) : WeatherRepository {

    private val freshnessThreshold = 30 * 60 * 1000L

    override suspend fun getWeather(): WeatherResponse {
        return try {
            val currentTimestamp = System.currentTimeMillis()

            val cachedWeather = weatherDao.getWeather()

            if (cachedWeather == null || !cachedWeather.timestamp?.let { isDataFresh(it, currentTimestamp) }!!) {
                val weatherResponse = apiService.getWeather()
                val weatherEntity = weatherResponse.toEntity().copy(timestamp = currentTimestamp)
                weatherDao.insertWeather(weatherEntity)
                return weatherResponse
            }

            cachedWeather.toDomain()
        } catch (e: Exception) {
            val cachedWeather = weatherDao.getWeather()
            cachedWeather?.toDomain() ?: throw e
        }
    }

    override suspend fun getWeatherByLocation(location: String): LocationResponse {
        return try {
            val currentTimestamp = System.currentTimeMillis()

            val isUpToDate = locationWeatherDao.isLocationWeatherUpToDate(location, currentTimestamp - freshnessThreshold)


            if (isUpToDate == 0) {
                val locationResponse = apiService.getWeatherByLocation(location)
                val locationWeatherEntity = locationResponse.toEntity().copy(timestamp = currentTimestamp)
                locationWeatherDao.insertLocationWeather(locationWeatherEntity)
                return locationResponse
            } else {
                val cachedLocationWeather = locationWeatherDao.getLocationWeather(location)
                return cachedLocationWeather.toDomain()
            }
        } catch (e: Exception) {
            val cachedLocationWeather = locationWeatherDao.getLocationWeather(location)
            return cachedLocationWeather.toDomain()
        }
    }


    override suspend fun getWeatherTommorowByLocation(location: String): LocationResponse {
        return apiService.getWeatherTommorowByLocation(location)
    }

    private fun convertGmtToLocal(timestampGmt: Long): Long {
        val instant = java.time.Instant.ofEpochMilli(timestampGmt)
        val localTime = instant.atZone(ZoneId.systemDefault())
        return localTime.toInstant().toEpochMilli()
    }


    private fun isDataFresh(timestampGmt: Long, currentTimestamp: Long): Boolean {
        val timestampLocal = convertGmtToLocal(timestampGmt)
        return currentTimestamp - timestampLocal < freshnessThreshold
    }
}
