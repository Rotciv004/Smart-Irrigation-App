package com.example.smartirrigation.feature.dashboard

import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.HumidityStatus

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val status: DeviceStatus? = null,
    val humidityStatus: HumidityStatus? = null,
    val localTargetHumidityDraft: Int = 0,
    val errorMessage: String? = null,
    val isSavingTarget: Boolean = false,
    val isSendingPumpCommand: Boolean = false,
    val isSendingModeCommand: Boolean = false,
    val host: String = "",
    val port: Int = 80,
)

