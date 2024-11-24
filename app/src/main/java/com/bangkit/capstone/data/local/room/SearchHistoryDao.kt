package com.bangkit.capstone.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistoryEntity: SearchHistoryEntity)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getAllSearchHistory(): List<SearchHistoryEntity>

    @Query("DELETE FROM history")
    suspend fun deleteAllSearchHistory()
}