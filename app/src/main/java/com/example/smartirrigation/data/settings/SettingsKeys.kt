package com.example.smartirrigation.data.settings

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val Host = stringPreferencesKey("host")
    val Port = intPreferencesKey("port")
    val AppThemeMode = stringPreferencesKey("app_theme_mode")
    val PollingIntervalMs = intPreferencesKey("polling_interval_ms")
}

