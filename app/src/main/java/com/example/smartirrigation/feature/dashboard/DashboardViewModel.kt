package com.example.smartirrigation.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.core.util.HumidityStatusCalculator
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.IrrigationMode
import com.example.smartirrigation.data.repository.DeviceRepository
import com.example.smartirrigation.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val deviceRepository: DeviceRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private var requestInFlight = false
    private var pollingIntervalMs = 2500L

    init {
        viewModelScope.launch {
            settingsRepository.observeConnectionConfig().collect { config ->
                _uiState.update { current ->
                    current.copy(host = config.host, port = config.port)
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.observePollingInterval().collect { interval ->
                pollingIntervalMs = interval.toLong()
            }
        }
    }

    fun loadStatus() {
        loadStatusInternal(showLoading = _uiState.value.status == null)
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                loadStatusInternal(showLoading = _uiState.value.status == null)
                delay(pollingIntervalMs)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun onTargetHumidityChanged(value: Int) {
        _uiState.update { it.copy(localTargetHumidityDraft = value.coerceIn(0, 100)) }
    }

    fun saveTargetHumidity() {
        val target = _uiState.value.localTargetHumidityDraft
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTarget = true, errorMessage = null) }
            when (val result = deviceRepository.setTargetHumidity(target)) {
                is NetworkResult.Success -> updateStatus(result.data, false)
                is NetworkResult.HttpError -> setCommandError("Could not save target humidity: ${result.message}", target = "target")
                is NetworkResult.NetworkError -> setCommandError(result.message, target = "target")
                is NetworkResult.UnknownError -> setCommandError(result.message, target = "target")
            }
        }
    }

    fun startPump() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingPumpCommand = true, errorMessage = null) }
            when (val result = deviceRepository.startPump()) {
                is NetworkResult.Success -> updateStatus(result.data, false)
                is NetworkResult.HttpError -> setCommandError("Could not start pump: ${result.message}", target = "pump")
                is NetworkResult.NetworkError -> setCommandError(result.message, target = "pump")
                is NetworkResult.UnknownError -> setCommandError(result.message, target = "pump")
            }
        }
    }

    fun stopPump() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingPumpCommand = true, errorMessage = null) }
            when (val result = deviceRepository.stopPump()) {
                is NetworkResult.Success -> updateStatus(result.data, false)
                is NetworkResult.HttpError -> setCommandError("Could not stop pump: ${result.message}", target = "pump")
                is NetworkResult.NetworkError -> setCommandError(result.message, target = "pump")
                is NetworkResult.UnknownError -> setCommandError(result.message, target = "pump")
            }
        }
    }

    fun setMode(mode: IrrigationMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingModeCommand = true, errorMessage = null) }
            when (val result = deviceRepository.setMode(mode)) {
                is NetworkResult.Success -> updateStatus(result.data, false)
                is NetworkResult.HttpError -> setCommandError("Could not change mode: ${result.message}", target = "mode")
                is NetworkResult.NetworkError -> setCommandError(result.message, target = "mode")
                is NetworkResult.UnknownError -> setCommandError(result.message, target = "mode")
            }
        }
    }

    fun refresh() {
        loadStatus()
    }

    private fun loadStatusInternal(showLoading: Boolean) {
        if (requestInFlight) return
        viewModelScope.launch {
            requestInFlight = true
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = null) }
            }
            when (val result = deviceRepository.getStatus()) {
                is NetworkResult.Success -> updateStatus(result.data, showLoading)
                is NetworkResult.HttpError -> setLoadFailure("Device error ${result.code}: ${result.message}")
                is NetworkResult.NetworkError -> setLoadFailure(result.message)
                is NetworkResult.UnknownError -> setLoadFailure(result.message)
            }
            requestInFlight = false
        }
    }

    private fun updateStatus(status: DeviceStatus, wasLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isConnected = true,
                status = status,
                humidityStatus = HumidityStatusCalculator.calculate(status.humidity, status.targetHumidity),
                localTargetHumidityDraft = status.targetHumidity,
                errorMessage = null,
                isSavingTarget = false,
                isSendingPumpCommand = false,
                isSendingModeCommand = false,
            )
        }
    }

    private fun setLoadFailure(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isConnected = false,
                errorMessage = message,
                isSavingTarget = false,
                isSendingPumpCommand = false,
                isSendingModeCommand = false,
            )
        }
    }

    private fun setCommandError(message: String, target: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isConnected = false,
                errorMessage = message,
                isSavingTarget = if (target == "target") false else it.isSavingTarget,
                isSendingPumpCommand = if (target == "pump") false else it.isSendingPumpCommand,
                isSendingModeCommand = if (target == "mode") false else it.isSendingModeCommand,
            )
        }
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }

    companion object {
        fun factory(
            deviceRepository: DeviceRepository,
            settingsRepository: SettingsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(deviceRepository, settingsRepository) as T
            }
        }
    }
}

