package com.bangkit.capstone.domain.use_case.insert_searchhistory

import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(
        title: String,
        subtitle: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            withContext(Dispatchers.IO) {
                repository.insertSearchHistory(
                    title = title,
                    subtitle = subtitle,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = timestamp
                )
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to insert search history: ${e.message}"))
        }
    }
}

