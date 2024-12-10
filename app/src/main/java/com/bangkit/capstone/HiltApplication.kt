package com.bangkit.capstone

import android.app.Application
import com.bangkit.capstone.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}