package com.bangkit.capstone.data.mapper

import com.bangkit.capstone.data.local.entity.WeatherEntity
import com.bangkit.capstone.data.remote.response.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        rain = this.rain,
        riskLevel = this.riskLevel,
        city = this.city,
        conditionType = this.conditionType,
        temperature = this.temperature,
        description = this.description,
        humidity = this.humidity,
        windDirection = this.windDirection,
        windSpeed = this.windSpeed,
        cloudiness = this.cloudiness,
        pressure = this.pressure,
        timestamp = this.timestamp?.let { parseWeatherTimestampToLong(it) }
    )
}

fun WeatherEntity.toDomain(): WeatherResponse {
    return WeatherResponse(
        rain = this.rain,
        riskLevel = this.riskLevel,
        city = this.city,
        conditionType = this.conditionType,
        temperature = this.temperature,
        description = this.description,
        humidity = this.humidity,
        windDirection = this.windDirection,
        windSpeed = this.windSpeed,
        cloudiness = this.cloudiness,
        pressure = this.pressure,
        timestamp = this.timestamp?.toString()
    )
}

fun parseWeatherTimestampToLong(timestamp: String): Long? {
    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    val date = format.parse(timestamp)
    return date?.time ?: 0L
}
