package com.example.smartirrigation.feature.dashboard

import com.example.smartirrigation.MainDispatcherRule
import com.example.smartirrigation.core.network.NetworkResult
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.HumidityStatus
import com.example.smartirrigation.data.model.IrrigationMode
import com.example.smartirrigation.feature.FakeDeviceRepository
import com.example.smartirrigation.feature.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val status = DeviceStatus(42, 45, false, IrrigationMode.AUTO, "ESP32 Pump 1")

    @Test
    fun status_load_success_updatesUiState() = runTest {
        val viewModel = DashboardViewModel(
            deviceRepository = FakeDeviceRepository(NetworkResult.Success(status)),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.loadStatus()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isConnected)
        assertEquals(status, viewModel.uiState.value.status)
        assertEquals(HumidityStatus.GOOD, viewModel.uiState.value.humidityStatus)
    }

    @Test
    fun command_success_updatesStatus() = runTest {
        val updatedStatus = status.copy(pumpOn = true)
        val viewModel = DashboardViewModel(
            deviceRepository = FakeDeviceRepository(
                statusResult = NetworkResult.Success(status),
                startPumpResult = NetworkResult.Success(updatedStatus),
            ),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.startPump()
        advanceUntilIdle()

        assertEquals(updatedStatus, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.status?.pumpOn == true)
    }

    @Test
    fun command_failure_setsError() = runTest {
        val viewModel = DashboardViewModel(
            deviceRepository = FakeDeviceRepository(
                statusResult = NetworkResult.Success(status),
                setModeResult = NetworkResult.NetworkError("Mode update failed"),
            ),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.setMode(IrrigationMode.MANUAL)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals("Mode update failed", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun load_failure_keepsDashboardInDisconnectedState() = runTest {
        val viewModel = DashboardViewModel(
            deviceRepository = FakeDeviceRepository(
                statusResult = NetworkResult.NetworkError("No saved device IP address."),
            ),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.loadStatus()
        advanceUntilIdle()

        assertTrue(!viewModel.uiState.value.isConnected)
        assertEquals("No saved device IP address.", viewModel.uiState.value.errorMessage)
        assertEquals(null, viewModel.uiState.value.status)
    }
}

