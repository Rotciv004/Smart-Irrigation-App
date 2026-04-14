package com.example.smartirrigation.feature.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartirrigation.app.AppContainer
import com.example.smartirrigation.core.theme.Dimens
import com.example.smartirrigation.data.repository.DeviceRepositoryImpl
import com.example.smartirrigation.feature.connect.ConnectScreen
import com.example.smartirrigation.feature.connect.ConnectViewModel
import com.example.smartirrigation.feature.dashboard.DashboardScreen
import com.example.smartirrigation.feature.dashboard.DashboardViewModel
import com.example.smartirrigation.feature.settings.SettingsScreen
import com.example.smartirrigation.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    appContainer: AppContainer,
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = AppDestination.fromRoute(currentBackStackEntry?.destination?.route)

    fun navigateTo(destination: AppDestination) {
        navController.navigate(destination.route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "SmartIrrigation",
                    modifier = Modifier.padding(
                        horizontal = Dimens.ScreenPadding,
                        vertical = Dimens.SectionSpacing,
                    ),
                )
                AppDestination.entries.forEach { destination ->
                    NavigationDrawerItem(
                        label = { Text(destination.title) },
                        selected = currentDestination == destination,
                        onClick = {
                            navigateTo(destination)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding),
                    )
                }
            }
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(currentDestination.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
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
                            navigateTo(AppDestination.Dashboard)
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
                            navigateTo(AppDestination.Connect)
                        },
                        onClearConnection = {
                            viewModel.clearConnection()
                            navigateTo(AppDestination.Connect)
                        },
                    )
                }
            }
        }
    }
}
