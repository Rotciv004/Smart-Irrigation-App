package com.example.smartirrigation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartirrigation.core.network.ApiConstants
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeThemeMode().collect { mode ->
                _uiState.update { it.copy(currentThemeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeConnectionConfig().collect { config ->
                _uiState.update { it.copy(host = config.host, port = config.port) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observePollingInterval().collect { interval ->
                _uiState.update { it.copy(pollingIntervalMs = interval) }
            }
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepository.saveThemeMode(mode)
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    fun setPollingInterval(value: Int) {
        val clamped = value.coerceIn(
            ApiConstants.MIN_POLLING_INTERVAL_MS,
            ApiConstants.MAX_POLLING_INTERVAL_MS,
        )
        viewModelScope.launch {
            settingsRepository.savePollingInterval(clamped)
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    fun clearConnection() {
        viewModelScope.launch {
            settingsRepository.clearConnectionConfig()
            _uiState.update {
                it.copy(
                    host = ApiConstants.DEFAULT_HOST,
                    port = ApiConstants.DEFAULT_PORT,
                    errorMessage = null,
                )
            }
        }
    }

    companion object {
        fun factory(settingsRepository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(settingsRepository) as T
                }
            }
    }
}

