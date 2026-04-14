package com.example.smartirrigation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetModeRequest(
    val mode: String,
)

