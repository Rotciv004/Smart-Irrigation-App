package com.example.smartirrigation.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartirrigation.app.AppContainer
import com.example.smartirrigation.data.repository.DeviceRepositoryImpl
import com.example.smartirrigation.feature.connect.ConnectScreen
import com.example.smartirrigation.feature.connect.ConnectViewModel
import com.example.smartirrigation.feature.dashboard.DashboardScreen
import com.example.smartirrigation.feature.dashboard.DashboardViewModel
import com.example.smartirrigation.feature.settings.SettingsScreen
import com.example.smartirrigation.feature.settings.SettingsViewModel

@Composable
fun AppNavHost(
    appContainer: AppContainer,
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(AppDestination.Connect.route) {
            val viewModel: ConnectViewModel = viewModel(
                factory = ConnectViewModel.factory(
                    settingsRepository = appContainer.settingsRepository,
                    deviceRepositoryImpl = appContainer.deviceRepository as DeviceRepositoryImpl,
                ),
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(uiState.successMessage) {
                if (uiState.successMessage?.startsWith("Connection saved") == true) {
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo(AppDestination.Connect.route) { inclusive = true }
                    }
                }
            }
            ConnectScreen(
                uiState = uiState,
                onHostChanged = viewModel::onHostChanged,
                onPortChanged = viewModel::onPortChanged,
                onTestConnection = viewModel::testConnection,
                onSaveAndContinue = viewModel::saveConnection,
            )
        }
        composable(AppDestination.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.factory(
                    deviceRepository = appContainer.deviceRepository,
                    settingsRepository = appContainer.settingsRepository,
                ),
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            DashboardScreen(
                uiState = uiState,
                onRefresh = viewModel::refresh,
                onStartPolling = viewModel::startPolling,
                onStopPolling = viewModel::stopPolling,
                onTargetChanged = viewModel::onTargetHumidityChanged,
                onSaveTarget = viewModel::saveTargetHumidity,
                onStartPump = viewModel::startPump,
                onStopPump = viewModel::stopPump,
                onSetMode = viewModel::setMode,
                onOpenSettings = { navController.navigate(AppDestination.Settings.route) },
            )
        }
        composable(AppDestination.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.factory(appContainer.settingsRepository),
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                uiState = uiState,
                onSetThemeMode = viewModel::setThemeMode,
                onSetPollingInterval = viewModel::setPollingInterval,
                onEditConnection = {
                    navController.navigate(AppDestination.Connect.route)
                },
                onClearConnection = viewModel::clearConnection,
                onBack = {
                    navController.navigate(AppDestination.Connect.route) {
                        popUpTo(AppDestination.Dashboard.route) { inclusive = true }
                    }
                },
            )
        }
    }
}


