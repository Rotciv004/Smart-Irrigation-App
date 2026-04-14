package com.example.smartirrigation.app

import android.app.Application

class SmartIrrigationApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}

