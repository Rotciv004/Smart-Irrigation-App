package com.example.smartirrigation.feature.connect

data class ConnectUiState(
    val host: String = "",
    val port: String = "80",
    val isTesting: Boolean = false,
    val testSucceeded: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSaveEnabled: Boolean = false,
    val hostValidationError: String? = null,
    val portValidationError: String? = null,
)

