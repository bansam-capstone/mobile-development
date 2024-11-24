package com.bangkit.capstone.domain.use_case.delete_searchhistory

import com.bangkit.capstone.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
){
    suspend operator fun invoke() = repository.deleteSearchHistory()
}