package com.bangkit.capstone.domain.use_case.get_searchhistory

import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<Resource<List<SearchHistoryEntity>>> = flow {
        try {
            emit(Resource.Loading())
            val searchHistory = repository.getAllSearchHistory()
            emit(Resource.Success(searchHistory))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to retrieve search history: ${e.message}"))
        }
    }
}
