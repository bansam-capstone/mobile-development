package com.bangkit.capstone.data.repository

import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.data.local.room.SearchHistoryDao
import com.bangkit.capstone.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override suspend fun insertSearchHistory(
        title: String,
        subtitle: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ) {
        withContext(Dispatchers.IO) {
            val searchHistoryEntity = SearchHistoryEntity(
                title = title,
                subtitle = subtitle,
                latitude = latitude,
                longitude = longitude,
                timestamp = timestamp
            )
            searchHistoryDao.insertSearchHistory(searchHistoryEntity)
        }
    }

    override suspend fun getAllSearchHistory(): List<SearchHistoryEntity> {
        return withContext(Dispatchers.IO) {
            searchHistoryDao.getAllSearchHistory()
        }
    }

    override suspend fun deleteSearchHistory() {
        withContext(Dispatchers.IO) {
            searchHistoryDao.deleteAllSearchHistory()
        }
    }
}
