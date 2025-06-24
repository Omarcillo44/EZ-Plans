package com.ezsoftware.ezplans.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// Mantiene la estructura para el estado del tema
data class ThemeState(
    val isDarkTheme: Boolean,
    val useDynamicColor: Boolean
)

val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("No ThemeState provided")
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EZplansTheme(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeState.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (themeState.isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeState.isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Proporcionar el estado del tema a través del CompositionLocal
    CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}