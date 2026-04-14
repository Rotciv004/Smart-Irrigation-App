package com.example.smartirrigation.feature

import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.IrrigationMode
import com.example.smartirrigation.data.repository.DeviceRepository
import com.example.smartirrigation.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(
    initialConfig: DeviceConnectionConfig = DeviceConnectionConfig("", 80),
    initialTheme: AppThemeMode = AppThemeMode.SYSTEM,
    initialPolling: Int = 2500,
) : SettingsRepository {
    private val configFlow = MutableStateFlow(initialConfig)
    private val themeFlow = MutableStateFlow(initialTheme)
    private val pollingFlow = MutableStateFlow(initialPolling)

    override fun observeConnectionConfig(): Flow<DeviceConnectionConfig> = configFlow
    override suspend fun saveConnectionConfig(config: DeviceConnectionConfig) { configFlow.value = config }
    override suspend fun clearConnectionConfig() { configFlow.value = DeviceConnectionConfig("", 80) }
    override fun observeThemeMode(): Flow<AppThemeMode> = themeFlow
    override suspend fun saveThemeMode(mode: AppThemeMode) { themeFlow.value = mode }
    override fun observePollingInterval(): Flow<Int> = pollingFlow
    override suspend fun savePollingInterval(value: Int) { pollingFlow.value = value }
}

class FakeDeviceRepository(
    var statusResult: NetworkResult<DeviceStatus>,
    var setTargetResult: NetworkResult<DeviceStatus> = statusResult,
    var startPumpResult: NetworkResult<DeviceStatus> = statusResult,
    var stopPumpResult: NetworkResult<DeviceStatus> = statusResult,
    var setModeResult: NetworkResult<DeviceStatus> = statusResult,
) : DeviceRepository {
    override suspend fun getStatus(): NetworkResult<DeviceStatus> = statusResult
    override suspend fun setTargetHumidity(value: Int): NetworkResult<DeviceStatus> = setTargetResult
    override suspend fun startPump(): NetworkResult<DeviceStatus> = startPumpResult
    override suspend fun stopPump(): NetworkResult<DeviceStatus> = stopPumpResult
    override suspend fun setMode(mode: IrrigationMode): NetworkResult<DeviceStatus> = setModeResult
}

