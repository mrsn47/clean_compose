package com.example.compose_clean.nav

import com.example.compose_clean.R

sealed class Screen(val route: String) {
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Posts : Screen("posts")
    object Details : Screen("post_details")
}
