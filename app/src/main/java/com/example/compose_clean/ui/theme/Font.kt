package com.example.compose_clean.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.compose_clean.R

val Font = FontFamily(
    Font(R.font.inria_sans_regular),
    Font(R.font.inria_sans_italic, style = FontStyle.Italic),
    Font(R.font.inria_sans_bold, weight = FontWeight.Bold),
    Font(R.font.inria_sans_bold_italic, weight = FontWeight.Bold,  style = FontStyle.Italic),
    Font(R.font.inria_sans_light, weight = FontWeight.Light),
    Font(R.font.inria_sans_light_italic, weight = FontWeight.Light, style = FontStyle.Italic)
)