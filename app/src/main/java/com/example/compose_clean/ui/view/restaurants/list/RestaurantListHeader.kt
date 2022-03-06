package com.example.compose_clean.ui.view.restaurants.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose_clean.ui.theme.Typography

@Composable
fun RestaurantListHeader(type: String) {

    Box(modifier = Modifier.fillMaxWidth()
        .background(MaterialTheme.colors.surface)
        .padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
    ) {
        Text(type, style = Typography.h3)
    }

}