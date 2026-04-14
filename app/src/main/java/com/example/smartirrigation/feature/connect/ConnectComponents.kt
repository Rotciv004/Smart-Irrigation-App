package com.example.smartirrigation.feature.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.core.ui.SectionCard

@Composable
fun ConnectionMessageCard(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMicro)) {
            Text(
                text = if (isError) "Error" else "Success",
                style = MaterialTheme.typography.titleMedium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectionMessageCardPreview() {
    SmartIrrigationTheme {
        ConnectionMessageCard(
            message = "Connection successful.",
            isError = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


