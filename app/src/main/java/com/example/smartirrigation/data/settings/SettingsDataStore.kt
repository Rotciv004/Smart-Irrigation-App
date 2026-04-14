package com.example.smartirrigation.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    val connectionConfig: Flow<DeviceConnectionConfig> = dataStore.data.map { preferences ->
        DeviceConnectionConfig(
            host = preferences[SettingsKeys.Host] ?: ApiConstants.DEFAULT_HOST,
            port = preferences[SettingsKeys.Port] ?: ApiConstants.DEFAULT_PORT,
        )
    }

    val themeMode: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        preferences[SettingsKeys.AppThemeMode]
            ?.let(AppThemeMode::valueOf)
            ?: ApiConstants.DEFAULT_THEME_MODE
    }

    val pollingIntervalMs: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SettingsKeys.PollingIntervalMs] ?: ApiConstants.DEFAULT_POLLING_INTERVAL_MS
    }

    suspend fun saveConnectionConfig(config: DeviceConnectionConfig) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.Host] = config.host
            preferences[SettingsKeys.Port] = config.port
        }
    }

    suspend fun clearConnectionConfig() {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.Host] = ApiConstants.DEFAULT_HOST
            preferences[SettingsKeys.Port] = ApiConstants.DEFAULT_PORT
        }
    }

    suspend fun saveThemeMode(mode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.AppThemeMode] = mode.name
        }
    }

    suspend fun savePollingInterval(value: Int) {
        dataStore.edit { preferences ->
            preferences[SettingsKeys.PollingIntervalMs] = value
        }
    }

    companion object {
        fun create(appContext: android.content.Context): SettingsDataStore {
            val store = PreferenceDataStoreFactory.create(
                corruptionHandler = null,
                scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
                produceFile = { appContext.preferencesDataStoreFile("smart_irrigation.preferences_pb") },
            )
            return SettingsDataStore(store)
        }
    }
}


