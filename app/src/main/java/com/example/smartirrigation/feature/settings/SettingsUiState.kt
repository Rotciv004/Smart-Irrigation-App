package com.example.smartirrigation.feature.settings

import com.example.smartirrigation.data.model.AppThemeMode

data class SettingsUiState(
    val currentThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val host: String = "",
    val port: Int = 80,
    val pollingIntervalMs: Int = 2500,
    val errorMessage: String? = null,
)

