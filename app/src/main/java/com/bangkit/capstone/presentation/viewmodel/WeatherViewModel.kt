package com.bangkit.capstone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.capstone.domain.use_case.get_weather.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {


}
