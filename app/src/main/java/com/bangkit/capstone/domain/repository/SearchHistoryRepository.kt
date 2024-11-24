package com.bangkit.capstone.domain.repository

import com.bangkit.capstone.data.local.entity.SearchHistoryEntity

interface SearchHistoryRepository {

    suspend fun insertSearchHistory(title: String, subtitle: String, latitude: Double, longitude: Double, timestamp: Long)

    suspend fun getAllSearchHistory() : List<SearchHistoryEntity>

    suspend fun deleteSearchHistory()

}