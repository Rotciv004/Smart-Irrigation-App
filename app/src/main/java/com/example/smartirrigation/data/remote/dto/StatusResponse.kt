package com.example.smartirrigation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val humidity: Int,
    val targetHumidity: Int,
    val pumpOn: Boolean,
    val mode: String,
    val deviceName: String? = null,
)

