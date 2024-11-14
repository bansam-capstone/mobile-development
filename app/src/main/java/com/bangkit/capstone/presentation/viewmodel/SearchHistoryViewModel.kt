package com.bangkit.capstone.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.domain.use_case.get_searchhistory.GetSearchHistoryUseCase
import com.bangkit.capstone.domain.use_case.insert_searchhistory.InsertSearchHistoryUseCase
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val insertSearchHistoryUseCase: InsertSearchHistoryUseCase
) : ViewModel() {

    private val _searchHistoryState = MutableStateFlow<Resource<List<SearchHistoryEntity>>>(Resource.Loading())
    val searchHistoryState: StateFlow<Resource<List<SearchHistoryEntity>>> = _searchHistoryState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        getSearchHistory()
    }

    private fun getSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            getSearchHistoryUseCase().collectLatest { result ->
                _searchHistoryState.value = result
                _isLoading.value = result is Resource.Loading
                if (result is Resource.Error) {
                    _error.value = result.message
                } else {
                    _error.value = null
                }
            }
        }
    }

    fun insertSearchHistory(title: String, subtitle: String, latitude: Double, longitude: Double, timestamp: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("SearchHistoryViewModel", "Inserting search history: $title")
            try {
                insertSearchHistoryUseCase(title, subtitle, latitude, longitude, timestamp).collect { result ->
                    if (result is Resource.Error) {
                        _error.value = result.message
                        Log.e("SearchHistoryViewModel", "Error: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to insert search history: ${e.message}"
                Log.e("SearchHistoryViewModel", "Exception: ${e.message}")
            }
        }
    }
}
