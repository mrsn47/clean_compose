package com.example.compose_clean.ui.view.restaurants.city

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose_clean.ui.theme.darkGray

@Composable
fun CityItem(
    city: String,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.background)
            .clickable {
                onClick(city)
            }
    )
    {
        Text(
            text = city,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 8.dp)
        )
    }
    Divider(color = darkGray, thickness = 1.dp)
}