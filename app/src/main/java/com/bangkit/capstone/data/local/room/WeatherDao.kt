package com.bangkit.capstone.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.capstone.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    suspend fun getWeather(): WeatherEntity?

    @Query("SELECT COUNT(*) FROM weather WHERE timestamp > :threshold")
    suspend fun isWeatherUpToDate(threshold: Long): Int
}
