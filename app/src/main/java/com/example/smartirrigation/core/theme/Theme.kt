package com.example.smartirrigation.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryGreenContainer,
    onPrimaryContainer = OnPrimaryGreenContainer,
    secondary = SecondarySage,
    onSecondary = OnSecondarySage,
    secondaryContainer = SecondarySageContainer,
    onSecondaryContainer = OnSecondarySageContainer,
    tertiary = TertiaryTeal,
    onTertiary = OnTertiaryTeal,
    tertiaryContainer = TertiaryTealContainer,
    onTertiaryContainer = OnTertiaryTealContainer,
    background = AppBackground,
    onBackground = AppOnSurface,
    surface = AppSurface,
    onSurface = AppOnSurface,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppOnSurfaceVariant,
    outline = AppOutline,
    error = ErrorStatusColor,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenContainer,
    onPrimary = OnPrimaryGreenContainer,
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = OnPrimaryGreen,
    secondary = SecondarySageContainer,
    onSecondary = OnSecondarySageContainer,
    secondaryContainer = SecondarySage,
    onSecondaryContainer = OnSecondarySage,
    tertiary = TertiaryTealContainer,
    onTertiary = OnTertiaryTealContainer,
    tertiaryContainer = TertiaryTeal,
    onTertiaryContainer = OnTertiaryTeal,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorStatusColor,
)

private val SmartIrrigationShapes = Shapes(
    small = RoundedCornerShape(Dimens.ButtonCornerRadius),
    medium = RoundedCornerShape(Dimens.ButtonCornerRadius),
    large = RoundedCornerShape(Dimens.CardCornerRadius),
)

@Composable
fun SmartIrrigationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SmartIrrigationTypography,
        shapes = SmartIrrigationShapes,
        content = content,
    )
}


