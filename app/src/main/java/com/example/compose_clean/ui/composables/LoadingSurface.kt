package com.example.compose_clean.ui.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Composable
fun LoadingSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val randomFloat = rememberSaveable { mutableStateOf((5..20).random() / 100f) }.value

    val color by infiniteTransition.animateColor(
        initialValue = Color.Black.copy(alpha = randomFloat), targetValue = Color.Black.copy(alpha = 0.01f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Surface(
        color = color,
        modifier = modifier,
        shape = shape
    ) {}
}