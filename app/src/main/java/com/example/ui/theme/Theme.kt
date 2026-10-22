package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DreamColorScheme = darkColorScheme(
    primary = DreamNeonViolet,
    onPrimary = DreamDarkBackground,
    primaryContainer = DreamNeonVioletDark,
    onPrimaryContainer = DreamNeonVioletLight,

    secondary = DreamNeonCyan,
    onSecondary = DreamDarkBackground,
    secondaryContainer = DreamNeonCyanDark,
    onSecondaryContainer = DreamNeonCyanLight,

    tertiary = DreamNeonPink,
    onTertiary = DreamDarkBackground,

    background = DreamDarkBackground,
    onBackground = DreamTextPrimary,

    surface = DreamDarkSurface,
    onSurface = DreamTextPrimary,
    surfaceVariant = DreamDarkSurfaceElevated,
    onSurfaceVariant = DreamTextSecondary,

    outline = DreamDarkSurfaceBorder,
    error = DreamError,
    errorContainer = DreamErrorBg,
    onError = DreamTextPrimary
)

@Composable
fun DreamBoxTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DreamDarkBackground.toArgb()
                window.navigationBarColor = DreamDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = DreamColorScheme,
        typography = Typography,
        content = content
    )
}
