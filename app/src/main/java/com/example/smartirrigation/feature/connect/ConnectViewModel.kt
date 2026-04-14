package com.example.smartirrigation.feature.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.core.util.IpAddressValidator
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.repository.DeviceRepositoryImpl
import com.example.smartirrigation.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectViewModel(
    private val settingsRepository: SettingsRepository,
    private val deviceRepositoryImpl: DeviceRepositoryImpl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState())
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    fun onHostChanged(value: String) {
        _uiState.update {
            val updated = it.copy(
                host = value.trim(),
                testSucceeded = false,
                successMessage = null,
                errorMessage = null,
            )
            updated.copy(
                hostValidationError = validateHost(updated.host),
                isSaveEnabled = canSave(updated.host, updated.port),
            )
        }
    }

    fun onPortChanged(value: String) {
        _uiState.update {
            val updated = it.copy(
                port = value.filter { char -> char.isDigit() },
                testSucceeded = false,
                successMessage = null,
                errorMessage = null,
            )
            updated.copy(
                portValidationError = validatePort(updated.port),
                isSaveEnabled = canSave(updated.host, updated.port),
            )
        }
    }

    fun testConnection() {
        val state = _uiState.value
        val hostError = validateHost(state.host)
        val portError = validatePort(state.port)
        if (hostError != null || portError != null) {
            _uiState.update {
                it.copy(
                    hostValidationError = hostError,
                    portValidationError = portError,
                    errorMessage = "Enter a valid IPv4 address and port before testing.",
                    successMessage = null,
                    testSucceeded = false,
                    isSaveEnabled = canSave(it.host, it.port),
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTesting = true,
                    errorMessage = null,
                    successMessage = null,
                    testSucceeded = false,
                    isSaveEnabled = canSave(it.host, it.port),
                )
            }
            val result = deviceRepositoryImpl.testConnection(
                DeviceConnectionConfig(
                    host = state.host,
                    port = state.port.toInt(),
                ),
            )
            _uiState.update {
                when (result) {
                    is NetworkResult.Success -> it.copy(
                        isTesting = false,
                        testSucceeded = true,
                        successMessage = "Connection successful.",
                        errorMessage = null,
                        isSaveEnabled = canSave(it.host, it.port),
                    )

                    is NetworkResult.HttpError -> it.copy(
                        isTesting = false,
                        testSucceeded = false,
                        successMessage = null,
                        errorMessage = "Device responded with HTTP ${result.code}: ${result.message}",
                        isSaveEnabled = canSave(it.host, it.port),
                    )

                    is NetworkResult.NetworkError -> it.copy(
                        isTesting = false,
                        testSucceeded = false,
                        successMessage = null,
                        errorMessage = result.message,
                        isSaveEnabled = canSave(it.host, it.port),
                    )

                    is NetworkResult.UnknownError -> it.copy(
                        isTesting = false,
                        testSucceeded = false,
                        successMessage = null,
                        errorMessage = result.message,
                        isSaveEnabled = canSave(it.host, it.port),
                    )
                }
            }
        }
    }

    fun saveConnection() {
        val state = _uiState.value
        if (!canSave(state.host, state.port)) {
            _uiState.update {
                it.copy(
                    hostValidationError = validateHost(it.host),
                    portValidationError = validatePort(it.port),
                    errorMessage = "Enter a valid IPv4 address and port before saving.",
                    successMessage = null,
                    isSaveEnabled = false,
                )
            }
            return
        }

        viewModelScope.launch {
            settingsRepository.saveConnectionConfig(
                DeviceConnectionConfig(
                    host = state.host,
                    port = state.port.toInt(),
                ),
            )
            _uiState.update {
                it.copy(
                    successMessage = if (it.testSucceeded) {
                        "Connection saved."
                    } else {
                        "Connection saved. You can explore the app and test the device later."
                    },
                    errorMessage = null,
                    isSaveEnabled = true,
                )
            }
        }
    }

    private fun validateHost(value: String): String? {
        return if (IpAddressValidator.isValid(value)) null else "Enter a valid IPv4 address."
    }

    private fun validatePort(value: String): String? {
        val port = value.toIntOrNull()
        return if (port != null && port in 1..65535) null else "Port must be between 1 and 65535."
    }

    private fun canSave(host: String, port: String): Boolean {
        return validateHost(host) == null && validatePort(port) == null
    }

    companion object {
        fun factory(
            settingsRepository: SettingsRepository,
            deviceRepositoryImpl: DeviceRepositoryImpl,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ConnectViewModel(settingsRepository, deviceRepositoryImpl) as T
            }
        }
    }
}


