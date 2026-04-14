package com.example.smartirrigation.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.core.ui.EmptyView
import com.example.smartirrigation.core.ui.ErrorView
import com.example.smartirrigation.core.ui.LoadingView
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.HumidityStatus
import com.example.smartirrigation.data.model.IrrigationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onTargetChanged: (Int) -> Unit,
    onSaveTarget: () -> Unit,
    onStartPump: () -> Unit,
    onStopPump: () -> Unit,
    onSetMode: (IrrigationMode) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DisposableEffect(Unit) {
        onStartPolling()
        onDispose { onStopPolling() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Smart Irrigation") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing),
        ) {
            ConnectionStatusCard(
                isConnected = uiState.isConnected,
                host = uiState.host,
                port = uiState.port,
                onRefresh = onRefresh,
            )

            when {
                uiState.isLoading && uiState.status == null -> LoadingView("Loading device status...")
                uiState.status != null && uiState.humidityStatus != null -> {
                    HumidityCard(status = uiState.status, humidityStatus = uiState.humidityStatus)
                    AutoWateringCard(
                        targetHumidity = uiState.localTargetHumidityDraft,
                        currentMode = uiState.status.mode,
                        isSaving = uiState.isSavingTarget,
                        onTargetChanged = onTargetChanged,
                        onSaveTarget = onSaveTarget,
                    )
                    PumpControlCard(
                        pumpOn = uiState.status.pumpOn,
                        currentMode = uiState.status.mode,
                        isSendingPumpCommand = uiState.isSendingPumpCommand,
                        isSendingModeCommand = uiState.isSendingModeCommand,
                        onStartPump = onStartPump,
                        onStopPump = onStopPump,
                        onSetMode = onSetMode,
                    )
                    DeviceInfoCard(status = uiState.status)
                }
                uiState.errorMessage != null -> EmptyView(
                    title = "Explore app offline",
                    message = "${uiState.errorMessage} You can still open Settings and the connection screen, then retry when the ESP32 is available.",
                )
                else -> EmptyView(
                    title = "Explore app offline",
                    message = "No live device status yet. You can browse the app now and connect the ESP32 later.",
                )
            }

            if (uiState.errorMessage != null && uiState.status != null) {
                ErrorView(uiState.errorMessage)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    SmartIrrigationTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                isConnected = true,
                status = DeviceStatus(
                    humidity = 42,
                    targetHumidity = 55,
                    pumpOn = false,
                    mode = IrrigationMode.AUTO,
                    deviceName = "ESP32 Pump 1",
                ),
                humidityStatus = HumidityStatus.DRY,
                localTargetHumidityDraft = 55,
                host = "192.168.1.55",
                port = 80,
            ),
            onRefresh = {},
            onStartPolling = {},
            onStopPolling = {},
            onTargetChanged = {},
            onSaveTarget = {},
            onStartPump = {},
            onStopPump = {},
            onSetMode = {},
            onOpenSettings = {},
        )
    }
}

