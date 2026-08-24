package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NovaDarkColorScheme = darkColorScheme(
    primary = NovaCyan,
    onPrimary = NovaVoidBlack,
    primaryContainer = NovaSurfaceElevated,
    onPrimaryContainer = NovaCyan,
    secondary = NovaViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = NovaViolet,
    tertiary = NovaLaserGreen,
    onTertiary = NovaVoidBlack,
    background = NovaVoidBlack,
    onBackground = NovaTextPrimary,
    surface = NovaSurfaceDark,
    onSurface = NovaTextPrimary,
    surfaceVariant = NovaSurfaceCard,
    onSurfaceVariant = NovaTextSecondary,
    outline = NovaBorder,
    error = NovaNeonPink,
    onError = Color.White
)

@Composable
fun NovaTheme(
    content: @Composable () -> Unit
) {
    // NOVA is strictly a futuristic dark-mode UI with sci-fi luminous aesthetics
    MaterialTheme(
        colorScheme = NovaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
