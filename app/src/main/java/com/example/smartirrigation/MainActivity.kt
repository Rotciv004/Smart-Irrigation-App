package com.example.smartirrigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartirrigation.app.SmartIrrigationApp
import com.example.smartirrigation.core.theme.SmartIrrigationTheme
import com.example.smartirrigation.data.model.AppThemeMode
import com.example.smartirrigation.feature.navigation.AppDestination
import com.example.smartirrigation.feature.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as SmartIrrigationApp).appContainer

        setContent {
            val themeMode = appContainer.settingsRepository
                .observeThemeMode()
                .collectAsStateWithLifecycle(initialValue = AppThemeMode.SYSTEM)
            val darkTheme = when (themeMode.value) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            SmartIrrigationTheme(darkTheme = darkTheme) {
                AppNavHost(
                    appContainer = appContainer,
                    startDestination = AppDestination.Dashboard.route,
                )
            }
        }
    }
}