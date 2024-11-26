package com.bangkit.capstone.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "location_weather")
@Parcelize
data class LocationWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rain: Double?,
    val riskLevel: String?,
    val conditionType: String?,
    val temperature: Double?,
    val description: String?,
    val humidity: Double?,
    val location: String?,
    val windDirection: Int?,
    val windSpeed: Double?,
    val cloudiness: Int?,
    val pressure: Double?,
    val timestamp: Long?
) : Parcelable