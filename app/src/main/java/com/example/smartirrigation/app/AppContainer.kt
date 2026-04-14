package com.example.smartirrigation.app

import android.content.Context
import com.example.smartirrigation.core.network.NetworkModule
import com.example.smartirrigation.data.repository.DeviceRepository
import com.example.smartirrigation.data.repository.DeviceRepositoryImpl
import com.example.smartirrigation.data.repository.SettingsRepository
import com.example.smartirrigation.data.repository.SettingsRepositoryImpl
import com.example.smartirrigation.data.settings.SettingsDataStore

interface AppContainer {
    val settingsRepository: SettingsRepository
    val deviceRepository: DeviceRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext
    private val settingsDataStore = SettingsDataStore.create(appContext)
    private val networkModule = NetworkModule()

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(
            settingsRepository = settingsRepository,
            networkModule = networkModule,
        )
    }
}

