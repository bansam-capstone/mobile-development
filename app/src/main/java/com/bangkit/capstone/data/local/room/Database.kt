package com.bangkit.capstone.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bangkit.capstone.data.local.entity.LocationWeatherEntity
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.data.local.entity.WeatherEntity

@Database(entities = [SearchHistoryEntity::class, WeatherEntity::class, LocationWeatherEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun weatherDao(): WeatherDao
    abstract fun locationWeatherDao(): LocationWeatherDao
}