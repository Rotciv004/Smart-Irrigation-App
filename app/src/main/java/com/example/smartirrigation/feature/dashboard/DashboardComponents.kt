package com.example.smartirrigation.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.core.theme.DryStatusColor
import com.example.smartirrigation.core.theme.GoodStatusColor
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.core.theme.WetStatusColor
import com.example.smartirrigation.core.ui.SectionCard
import com.example.smartirrigation.data.model.DeviceStatus
import com.example.smartirrigation.data.model.HumidityStatus
import com.example.smartirrigation.data.model.IrrigationMode

@Composable
fun ConnectionStatusCard(
    isConnected: Boolean,
    host: String,
    port: Int,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            Text("Connection", style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (isConnected) "Connected" else "Disconnected",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Text(
                text = "${if (host.isBlank()) "No saved IP" else host}:$port",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}

@Composable
fun HumidityCard(
    status: DeviceStatus,
    humidityStatus: HumidityStatus,
    modifier: Modifier = Modifier,
) {
    val statusColor = humidityStatus.toColor()
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            Text("Humidity", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${status.humidity}%",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = humidityStatus.label,
                style = MaterialTheme.typography.bodyLarge,
                color = statusColor,
            )
            LinearProgressIndicator(
                progress = { status.humidity / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Target humidity: ${status.targetHumidity}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AutoWateringCard(
    targetHumidity: Int,
    currentMode: IrrigationMode,
    isSaving: Boolean,
    onTargetChanged: (Int) -> Unit,
    onSaveTarget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            Text("Auto watering", style = MaterialTheme.typography.titleMedium)
            Text("Target humidity", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = targetHumidity.toFloat(),
                onValueChange = { onTargetChanged(it.toInt()) },
                valueRange = 0f..100f,
            )
            Text("$targetHumidity%", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Current mode: ${currentMode.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onSaveTarget, enabled = !isSaving) {
                Text("Save target")
            }
        }
    }
}

@Composable
fun PumpControlCard(
    pumpOn: Boolean,
    currentMode: IrrigationMode,
    isSendingPumpCommand: Boolean,
    isSendingModeCommand: Boolean,
    onStartPump: () -> Unit,
    onStopPump: () -> Unit,
    onSetMode: (IrrigationMode) -> Unit,
    helperMessage: String?,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            val nextMode = if (currentMode == IrrigationMode.AUTO) {
                IrrigationMode.MANUAL
            } else {
                IrrigationMode.AUTO
            }
            Text("Pump control", style = MaterialTheme.typography.titleMedium)
            Text("Pump state: ${if (pumpOn) "ON" else "OFF"}", style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
                Button(onClick = onStartPump, enabled = !isSendingPumpCommand) {
                    Text("Start pump")
                }
                OutlinedButton(onClick = onStopPump, enabled = !isSendingPumpCommand) {
                    Text("Stop pump")
                }
            }
            Text("Mode", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Current mode: ${currentMode.name}. Use the button below to switch to the other mode.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            helperMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = { onSetMode(nextMode) },
                enabled = !isSendingModeCommand,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Switch to ${nextMode.name}")
            }
        }
    }
}

@Composable
fun DeviceInfoCard(
    status: DeviceStatus,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            Text("Device info", style = MaterialTheme.typography.titleMedium)
            if (!status.deviceName.isNullOrBlank()) {
                Text("Device name: ${status.deviceName}", style = MaterialTheme.typography.bodyLarge)
            }
            Text("Current mode: ${status.mode.name}", style = MaterialTheme.typography.bodyMedium)
            Text("Pump state: ${if (status.pumpOn) "ON" else "OFF"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private val HumidityStatus.label: String
    get() = when (this) {
        HumidityStatus.DRY -> "Dry"
        HumidityStatus.GOOD -> "Good"
        HumidityStatus.WET -> "Wet"
    }

private fun HumidityStatus.toColor(): Color = when (this) {
    HumidityStatus.DRY -> DryStatusColor
    HumidityStatus.GOOD -> GoodStatusColor
    HumidityStatus.WET -> WetStatusColor
}

@Preview(showBackground = true)
@Composable
private fun DashboardCardsPreview() {
    SmartIrrigationTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall),
        ) {
            val status = DeviceStatus(
                humidity = 42,
                targetHumidity = 55,
                pumpOn = false,
                mode = IrrigationMode.AUTO,
                deviceName = "ESP32 Pump 1",
            )
            ConnectionStatusCard(true, "192.168.1.55", 80, onRefresh = {})
            HumidityCard(status, HumidityStatus.DRY)
            AutoWateringCard(55, IrrigationMode.AUTO, false, {}, {})
            PumpControlCard(false, IrrigationMode.AUTO, false, false, {}, {}, {}, null)
            DeviceInfoCard(status)
        }
    }
}

