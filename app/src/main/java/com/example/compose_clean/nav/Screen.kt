package com.example.compose_clean.nav

import com.example.compose_clean.R

sealed class Screen(val route: String) {
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Restaurants : Screen("restaurants")
    object RestaurantDetails : Screen("restaurant_details")
    object Cities : Screen("cities")
}
