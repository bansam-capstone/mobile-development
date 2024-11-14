package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase
) : ViewModel() {


}