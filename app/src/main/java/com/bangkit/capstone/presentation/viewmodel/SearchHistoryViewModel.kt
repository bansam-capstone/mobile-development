package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.domain.use_case.get_searchhistory.GetSearchHistoryUseCase
import com.bangkit.capstone.domain.use_case.insert_searchhistory.InsertSearchHistoryUseCase
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.domain.use_case.delete_searchhistory.DeleteSearchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val insertSearchHistoryUseCase: InsertSearchHistoryUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase
) : ViewModel() {

    private val _searchHistoryState = MutableStateFlow<List<SearchHistoryEntity>>(emptyList())
    val searchHistoryState: StateFlow<List<SearchHistoryEntity>> = _searchHistoryState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        getSearchHistory()
    }


    fun getSearchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getSearchHistoryUseCase().collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> _isLoading.value = true
                        is Resource.Success -> {
                            _isLoading.value = false
                            _searchHistoryState.value = result.data ?: emptyList()
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _error.value = result.message
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Failed to fetch search history: ${e.message}"
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            try {
                deleteSearchHistoryUseCase()
                _searchHistoryState.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to clear search history: ${e.message}"
            }
        }
    }

    fun insertSearchHistory(
        title: String,
        subtitle: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                insertSearchHistoryUseCase(title, subtitle, latitude, longitude, timestamp).collect { result ->
                    when (result) {
                        is Resource.Loading -> _isLoading.value = true
                        is Resource.Success -> {
                            _isLoading.value = false
                            getSearchHistory()
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _error.value = result.message
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Failed to insert search history: ${e.message}"
            }
        }
    }
}

