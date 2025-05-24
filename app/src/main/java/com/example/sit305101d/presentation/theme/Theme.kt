package com.example.sit305101d.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Use custom colors for the light theme
private val LightColorScheme = lightColorScheme(
    primary = AppPrimaryColor,
    secondary = TextColorSecondary,
    tertiary = Color.Gray,
    background = AppBackgroundColorStart,
    surface = AppBackgroundColorStart,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = TextColorPrimary,
    onSurface = TextColorPrimary
)

// Dark theme can be defined later if needed, using LightColorScheme for now
private val DarkColorScheme = LightColorScheme

// Define a Gradient Brush for the background
val appBackgroundGradient = Brush.verticalGradient(
    colors = listOf(AppBackgroundColorStart, AppBackgroundColorEnd)
)

@Composable
fun SIT305101DTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to use custom theme colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Ensure edge-to-edge is enabled (usually default with ComponentActivity/Compose)
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
