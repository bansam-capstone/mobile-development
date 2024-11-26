package com.bangkit.capstone.data.mapper

import com.bangkit.capstone.data.local.entity.LocationWeatherEntity
import com.bangkit.capstone.data.remote.response.LocationResponse
import java.text.SimpleDateFormat
import java.util.*

fun LocationResponse.toEntity(): LocationWeatherEntity {
    return LocationWeatherEntity(
        rain = this.rain,
        riskLevel = this.riskLevel,
        location = this.location,
        conditionType = this.conditionType,
        temperature = this.temperature,
        description = this.description,
        humidity = this.humidity,
        windDirection = this.windDirection,
        windSpeed = this.windSpeed,
        cloudiness = this.cloudiness,
        pressure = this.pressure,
        timestamp = this.timestamp?.let { parseLocationTimestampToLong(it) }
    )
}

fun LocationWeatherEntity.toDomain(): LocationResponse {
    return LocationResponse(
        rain = this.rain,
        riskLevel = this.riskLevel,
        location = this.location,
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

fun parseLocationTimestampToLong(timestamp: String): Long {
    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    val date = format.parse(timestamp)
    return date?.time ?: 0L
}
