package com.example.compose_clean.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    body2 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    h6 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    h5 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    h4 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    button = TextStyle(
        fontFamily = Font,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)