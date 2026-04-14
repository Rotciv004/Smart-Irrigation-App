package com.example.smartirrigation.feature.navigation

enum class AppDestination(
    val route: String,
    val title: String,
) {
    Connect("connect", "Connect to device"),
    Dashboard("dashboard", "Smart Irrigation"),
    Settings("settings", "Settings"),
    ;

    companion object {
        fun fromRoute(route: String?): AppDestination =
            entries.firstOrNull { it.route == route } ?: Dashboard
    }
}

