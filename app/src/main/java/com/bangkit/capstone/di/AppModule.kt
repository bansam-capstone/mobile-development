package com.bangkit.capstone.di

import android.content.Context
import androidx.room.Room
import com.bangkit.capstone.data.local.room.SearchHistoryDao
import com.bangkit.capstone.data.local.room.Database
import com.bangkit.capstone.data.local.room.LocationWeatherDao
import com.bangkit.capstone.data.local.room.WeatherDao
import com.bangkit.capstone.data.remote.retrofit.ApiConfig
import com.bangkit.capstone.data.remote.retrofit.ApiService
import com.bangkit.capstone.data.repository.SearchHistoryRepositoryImpl
import com.bangkit.capstone.data.repository.WeatherRepositoryImpl
import com.bangkit.capstone.domain.repository.SearchHistoryRepository
import com.bangkit.capstone.domain.repository.WeatherRepository
import com.bangkit.capstone.domain.use_case.get_weather.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide Room Database
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext application: Context
    ): Database {
        return Room.databaseBuilder(
            application,
            Database::class.java,
            "bansam_db"
        ).build()
    }

    @Provides
    fun provideSearchHistoryDao(
        database: Database
    ): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    fun provideWeatherDao(
        database: Database
    ): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    fun provideLocationWeatherDao(
        database: Database
    ): LocationWeatherDao {
        return database.locationWeatherDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(searchHistoryDao: SearchHistoryDao): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(searchHistoryDao)
    }

    // Provide WeatherRepository implementation
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: ApiService,
        weatherDao: WeatherDao,
        locationWeatherDao: LocationWeatherDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, weatherDao, locationWeatherDao)
    }

    // Provide Retrofit ApiService
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
    fun provideGetWeatherUseCase(repository: WeatherRepository): GetWeatherUseCase {
        return GetWeatherUseCase(repository)
    }
}
