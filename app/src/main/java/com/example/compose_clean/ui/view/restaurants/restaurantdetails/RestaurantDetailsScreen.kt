package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RestaurantDetailsScreen(
    navController: NavController,
    id: String,
    restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {
    Text(text = "Test $id")

    LaunchedEffect(Unit) {
        restaurantDetailsViewModel.launchedEffect("1")
    }
}