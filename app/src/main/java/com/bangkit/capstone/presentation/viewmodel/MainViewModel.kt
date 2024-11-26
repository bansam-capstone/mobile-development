package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.capstone.domain.use_case.get_weatherbylocation.GetWeatherByLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherByLocationsUseCase: GetWeatherByLocationsUseCase
) : ViewModel() {


}