package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LevelUpColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleGlow,
    secondary = PurpleLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = PurpleGlow,
    tertiary = GoldXP,
    onTertiary = Color(0xFF1E1B4B),
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = GoldXPLight,
    background = Navy950,
    onBackground = TextPrimary,
    surface = Navy900,
    onSurface = TextPrimary,
    surfaceVariant = Navy800,
    onSurfaceVariant = TextSecondary,
    outline = Navy700,
    outlineVariant = Navy600,
    error = LevelError,
    onError = Color.White
)

@Composable
fun LevelUpTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Navy950.toArgb()
                it.navigationBarColor = Navy900.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = LevelUpColorScheme,
        typography = Typography,
        content = content
    )
}
