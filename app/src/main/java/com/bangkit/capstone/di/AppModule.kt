package com.bangkit.capstone.di

import com.bangkit.capstone.data.remote.retrofit.ApiConfig
import com.bangkit.capstone.data.remote.retrofit.ApiService
import com.bangkit.capstone.data.repository.WeatherRepositoryImpl
import com.bangkit.capstone.domain.repository.WeatherRepository
import com.bangkit.capstone.domain.use_case.get_weather.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiConfig(): ApiConfig {
        return ApiConfig()
    }

    @Provides
    @Singleton
    fun provideApiService(apiConfig: ApiConfig): ApiService {
        return apiConfig.getApiService()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: ApiService): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }

    @Provides
    fun provideGetWeatherUseCase(repository: WeatherRepository): GetWeatherUseCase {
        return GetWeatherUseCase(repository)
    }

}