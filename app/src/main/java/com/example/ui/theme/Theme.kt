package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ZaraPurple,
    onPrimary = ZaraTextPrimary,
    primaryContainer = ZaraSurfaceVariant,
    onPrimaryContainer = ZaraPurple,
    secondary = ZaraCyan,
    onSecondary = ZaraBackground,
    secondaryContainer = ZaraCardBorder,
    onSecondaryContainer = ZaraCyan,
    tertiary = ZaraPink,
    background = ZaraBackground,
    onBackground = ZaraTextPrimary,
    surface = ZaraSurface,
    onSurface = ZaraTextPrimary,
    surfaceVariant = ZaraSurfaceVariant,
    onSurfaceVariant = ZaraTextSecondary,
    error = ZaraRed,
    outline = ZaraCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ZaraPurple,
    onPrimary = ZaraTextPrimary,
    background = ZaraBackground,
    surface = ZaraSurface
)

@Composable
fun ZaraAITheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = ZaraBackground.toArgb()
                it.navigationBarColor = ZaraBackground.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
