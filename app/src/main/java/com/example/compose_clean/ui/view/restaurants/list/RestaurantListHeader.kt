package com.example.compose_clean.ui.view.restaurants.list

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose_clean.ui.composables.LoadingSurface
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.theme.Shapes
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.gray

@Composable
fun RestaurantListHeader(type: String) {

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.surface)
        .padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
    ) {
        Text(type, style = Typography.h3)
    }

}

@Composable
fun LoadingRestaurantListHeader() {

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.surface)
        .padding(start = 16.dp, bottom = 8.dp, top = 12.dp)
    ) {
        LoadingSurface(
            modifier = Modifier
                .height(28.spToDp())
                .width(100.spToDp()),
            shape = Shapes.small
        )
    }
}