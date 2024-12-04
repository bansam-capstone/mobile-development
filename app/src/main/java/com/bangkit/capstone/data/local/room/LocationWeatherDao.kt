package com.bangkit.capstone.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.capstone.data.local.entity.LocationWeatherEntity

@Dao
interface LocationWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationWeather(locationWeatherEntity: LocationWeatherEntity)

    @Query("SELECT * FROM location_weather WHERE location = :location ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLocationWeather(location: String): LocationWeatherEntity

    @Query("SELECT COUNT(*) FROM location_weather WHERE location = :location AND timestamp > :threshold")
    suspend fun isLocationWeatherUpToDate(location: String, threshold: Long): Int
}
