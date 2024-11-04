package com.bangkit.capstone.data.remote.retrofit

import com.bangkit.capstone.common.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    fun getApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL_WEATHER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}