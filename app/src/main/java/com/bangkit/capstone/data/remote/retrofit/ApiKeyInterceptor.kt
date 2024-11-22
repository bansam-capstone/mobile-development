package com.bangkit.capstone.data.remote.retrofit

import android.util.Log
import com.bangkit.capstone.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.API_WEATHER}")
            .method(original.method, original.body)
            .build()

        val response = chain.proceed(request)
        return response
    }
}