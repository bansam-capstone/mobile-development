package com.bangkit.capstone.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1, exportSchema = false)
abstract class SearchHistoryDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}