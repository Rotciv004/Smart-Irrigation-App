package com.example.smartirrigation.data.model

data class DeviceStatus(
    val humidity: Int,
    val targetHumidity: Int,
    val pumpOn: Boolean,
    val mode: IrrigationMode,
    val deviceName: String?,
)

