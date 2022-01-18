package com.example.compose_clean.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkTheme = darkColors(
    primary = Color(0xFFcba37e),
    primaryVariant = Color(0xFF533b23),
    secondary = Color(0xFF930101),
    onSurface = Color(0xFFA59D96),
    background = Color(0xFFFFFFFF)
)

private val LightTheme = lightColors(
    primary = Color(0xFFcba37e),
    primaryVariant = Color(0xFF930101),
    secondary = Color(0xFF44acee),
    onSurface = Color(0xFFA59D96),
    background = Color(0xFFFFFFFF)

)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkTheme
    } else {
        LightTheme
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}