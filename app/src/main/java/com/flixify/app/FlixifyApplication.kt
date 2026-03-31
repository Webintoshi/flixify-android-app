package com.flixify.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlixifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}
