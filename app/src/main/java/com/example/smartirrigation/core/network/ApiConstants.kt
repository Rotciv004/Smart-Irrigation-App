package com.example.smartirrigation.core.network

import com.example.smartirrigation.data.model.AppThemeMode

object ApiConstants {
    const val DEFAULT_HOST = ""
    const val DEFAULT_PORT = 80
    const val DEFAULT_POLLING_INTERVAL_MS = 2500
    const val MIN_POLLING_INTERVAL_MS = 1000
    const val MAX_POLLING_INTERVAL_MS = 10000
    const val REQUEST_TIMEOUT_SECONDS = 5L

    const val STATUS_ENDPOINT = "status"
    const val TARGET_HUMIDITY_ENDPOINT = "target-humidity"
    const val PUMP_START_ENDPOINT = "pump/start"
    const val PUMP_STOP_ENDPOINT = "pump/stop"
    const val MODE_ENDPOINT = "mode"

    val DEFAULT_THEME_MODE = AppThemeMode.SYSTEM
}

