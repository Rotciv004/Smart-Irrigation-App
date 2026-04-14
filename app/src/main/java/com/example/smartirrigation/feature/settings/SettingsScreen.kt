package com.example.smartirrigation.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.core.util.TimeFormatters
import com.example.smartirrigation.data.model.AppThemeMode

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onSetPollingInterval: (Int) -> Unit,
    onEditConnection: () -> Unit,
    onClearConnection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pollingText by remember(uiState.pollingIntervalMs) { mutableStateOf(uiState.pollingIntervalMs.toString()) }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing),
    ) {
        SettingsSectionCard(title = "Appearance") {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
                AppThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiState.currentThemeMode == mode,
                        onClick = { onSetThemeMode(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar(Char::uppercase)) },
                    )
                }
            }
        }

        SettingsSectionCard(title = "Device configuration") {
            Text("Saved IP: ${uiState.host.ifBlank { "Not set" }}", style = MaterialTheme.typography.bodyLarge)
            Text("Saved port: ${uiState.port}", style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onEditConnection) {
                Text("Edit connection")
            }
        }

        SettingsSectionCard(title = "App behavior") {
            OutlinedTextField(
                value = pollingText,
                onValueChange = {
                    pollingText = it.filter(Char::isDigit)
                    localError = null
                },
                label = { Text("Polling interval (ms)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    Text(localError ?: "Allowed range: 1000 to 10000 ms")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Current value: ${TimeFormatters.formatPollingInterval(uiState.pollingIntervalMs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {
                val parsed = pollingText.toIntOrNull()
                if (parsed == null) {
                    localError = "Enter a valid polling interval."
                } else {
                    onSetPollingInterval(parsed)
                    pollingText = parsed.coerceIn(1000, 10000).toString()
                    localError = null
                }
            }) {
                Text("Save polling interval")
            }
        }

        SettingsSectionCard(title = "About") {
            Text("SmartIrrigation", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Local Wi-Fi control app for ESP32 irrigation system",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        uiState.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onClearConnection,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Clear saved connection")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SmartIrrigationTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                currentThemeMode = AppThemeMode.SYSTEM,
                host = "192.168.1.55",
                port = 80,
                pollingIntervalMs = 2500,
            ),
            onSetThemeMode = {},
            onSetPollingInterval = {},
            onEditConnection = {},
            onClearConnection = {},
        )
    }
}

