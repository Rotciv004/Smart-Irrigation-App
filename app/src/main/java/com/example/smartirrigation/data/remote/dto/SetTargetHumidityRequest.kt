package com.example.smartirrigation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetTargetHumidityRequest(
    val targetHumidity: Int,
)

