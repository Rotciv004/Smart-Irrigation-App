package com.example.smartirrigation.data.repository

import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeConnectionConfig(): Flow<DeviceConnectionConfig>
    suspend fun saveConnectionConfig(config: DeviceConnectionConfig)
    suspend fun clearConnectionConfig()
    fun observeThemeMode(): Flow<AppThemeMode>
    suspend fun saveThemeMode(mode: AppThemeMode)
    fun observePollingInterval(): Flow<Int>
    suspend fun savePollingInterval(value: Int)
}

