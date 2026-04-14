package com.example.smartirrigation.data.repository

import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.settings.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {
    override fun observeConnectionConfig(): Flow<DeviceConnectionConfig> =
        settingsDataStore.connectionConfig

    override suspend fun saveConnectionConfig(config: DeviceConnectionConfig) {
        settingsDataStore.saveConnectionConfig(config)
    }

    override suspend fun clearConnectionConfig() {
        settingsDataStore.clearConnectionConfig()
    }

    override fun observeThemeMode(): Flow<AppThemeMode> = settingsDataStore.themeMode

    override suspend fun saveThemeMode(mode: AppThemeMode) {
        settingsDataStore.saveThemeMode(mode)
    }

    override fun observePollingInterval(): Flow<Int> =
        settingsDataStore.pollingIntervalMs.map { interval ->
            interval.coerceIn(
                ApiConstants.MIN_POLLING_INTERVAL_MS,
                ApiConstants.MAX_POLLING_INTERVAL_MS,
            )
        }

    override suspend fun savePollingInterval(value: Int) {
        settingsDataStore.savePollingInterval(
            value.coerceIn(
                ApiConstants.MIN_POLLING_INTERVAL_MS,
                ApiConstants.MAX_POLLING_INTERVAL_MS,
            ),
        )
    }
}

