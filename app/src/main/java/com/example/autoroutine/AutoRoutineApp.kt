package com.example.autoroutine

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutoRoutineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize background service scheduling here if needed
    }
}
