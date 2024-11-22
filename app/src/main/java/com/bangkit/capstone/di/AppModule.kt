package com.bangkit.capstone.di

import android.content.Context
import androidx.room.Room
import com.bangkit.capstone.data.local.room.SearchHistoryDao
import com.bangkit.capstone.data.local.room.SearchHistoryDatabase
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

    // Dari Database Room
    @Provides
    @Singleton
    fun provideSearchHistoryDatabase(
        @ApplicationContext application: Context
    ): SearchHistoryDatabase {
        return Room.databaseBuilder(
            application,
            SearchHistoryDatabase::class.java,
            "capstone_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSearchHistoryDao(
        database: SearchHistoryDatabase
    ): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(searchHistoryDao: SearchHistoryDao): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(searchHistoryDao)
    }

    // Dari Retrofit

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