package com.example.smartirrigation.feature.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.core.ui.LoadingView

@Composable
fun ConnectScreen(
    uiState: ConnectUiState,
    onHostChanged: (String) -> Unit,
    onPortChanged: (String) -> Unit,
    onTestConnection: () -> Unit,
    onSaveAndContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)) {
            Text("Connect to device", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Make sure your phone and ESP32 are connected to the same Wi-Fi network. You can also save the settings now and explore the app offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)) {
            OutlinedTextField(
                value = uiState.host,
                onValueChange = onHostChanged,
                label = { Text("IPv4 address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.hostValidationError != null,
                supportingText = {
                    uiState.hostValidationError?.let { Text(it) }
                },
            )
            OutlinedTextField(
                value = uiState.port,
                onValueChange = onPortChanged,
                label = { Text("Port") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.portValidationError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    uiState.portValidationError?.let { Text(it) }
                },
            )
            Button(
                onClick = onTestConnection,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isTesting,
            ) {
                Text("Test connection")
            }
            Button(
                onClick = onSaveAndContinue,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isSaveEnabled && !uiState.isTesting,
            ) {
                Text("Save and continue")
            }
            Text(
                text = "Testing the connection is optional. Save a valid IP and port to continue browsing the app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (uiState.isTesting) {
            LoadingView(message = "Testing local connection...")
        }

        uiState.errorMessage?.let {
            ConnectionMessageCard(message = it, isError = true)
        }
        uiState.successMessage?.let {
            ConnectionMessageCard(message = it, isError = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectScreenPreview() {
    SmartIrrigationTheme {
        ConnectScreen(
            uiState = ConnectUiState(
                host = "192.168.1.55",
                port = "80",
                successMessage = "Connection saved. You can explore the app and test the device later.",
                isSaveEnabled = true,
            ),
            onHostChanged = {},
            onPortChanged = {},
            onTestConnection = {},
            onSaveAndContinue = {},
        )
    }
}

