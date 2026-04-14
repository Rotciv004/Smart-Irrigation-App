package com.example.smartirrigation.feature.connect

import com.example.smartirrigation.MainDispatcherRule
import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.data.model.DeviceConnectionConfig
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.IrrigationMode
import com.example.smartirrigation.data.repository.DeviceRepositoryImpl
import com.example.smartirrigation.feature.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun invalid_host_preventsSave() = runTest {
        val viewModel = ConnectViewModel(
            settingsRepository = FakeSettingsRepository(),
            deviceRepositoryImpl = object : DeviceRepositoryImpl(FakeSettingsRepository(), com.example.smartirrigation.core.network.NetworkModule()) {
                override suspend fun testConnection(config: DeviceConnectionConfig): NetworkResult<DeviceStatus> {
                    return NetworkResult.NetworkError("Should not be called")
                }
            },
        )

        viewModel.onHostChanged("http://192.168.1.55")
        viewModel.onPortChanged("80")
        viewModel.testConnection()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSaveEnabled)
    }

    @Test
    fun valid_host_and_successful_test_enablesSave() = runTest {
        val successStatus = DeviceStatus(45, 50, false, IrrigationMode.AUTO, "ESP32 Pump 1")
        val viewModel = ConnectViewModel(
            settingsRepository = FakeSettingsRepository(),
            deviceRepositoryImpl = object : DeviceRepositoryImpl(FakeSettingsRepository(), com.example.smartirrigation.core.network.NetworkModule()) {
                override suspend fun testConnection(config: DeviceConnectionConfig): NetworkResult<DeviceStatus> {
                    return NetworkResult.Success(successStatus)
                }
            },
        )

        viewModel.onHostChanged("192.168.1.55")
        viewModel.onPortChanged("80")
        viewModel.testConnection()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaveEnabled)
        assertTrue(viewModel.uiState.value.testSucceeded)
    }

    @Test
    fun valid_host_and_port_enableSave_withoutLiveDevice() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val viewModel = ConnectViewModel(
            settingsRepository = settingsRepository,
            deviceRepositoryImpl = object : DeviceRepositoryImpl(FakeSettingsRepository(), com.example.smartirrigation.core.network.NetworkModule()) {
                override suspend fun testConnection(config: DeviceConnectionConfig): NetworkResult<DeviceStatus> {
                    return NetworkResult.NetworkError("Device offline")
                }
            },
        )

        viewModel.onHostChanged("192.168.1.55")
        viewModel.onPortChanged("80")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaveEnabled)

        viewModel.saveConnection()
        advanceUntilIdle()

        assertEquals("192.168.1.55", settingsRepository.observeConnectionConfig().value.host)
        assertEquals(80, settingsRepository.observeConnectionConfig().value.port)
    }
}

