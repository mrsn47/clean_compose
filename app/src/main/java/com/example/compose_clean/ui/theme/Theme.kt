package com.example.compose_clean.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// todo: define dark theme
private val DarkTheme = darkColors(
    primary = Color(0xFFcba37e),
    primaryVariant = Color(0xFF533b23),
    secondary = Color(0xFF930101),
    onSurface = Color(0xFFA59D96),
    background = Color(0xFF161616)
)

private val LightTheme = lightColors(
    primary = lightBrown,
    primaryVariant = bronze,
    secondary = bordeaux,
    secondaryVariant = lightBordeaux,
    onSecondary = white,
    surface = gray,
    background = white
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
//    val colors = if (darkTheme) {
//        DarkTheme
//    } else {
//        LightTheme
//    }
    val colors = LightTheme
    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}