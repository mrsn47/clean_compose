package com.example.compose_clean.nav

sealed class Screen(val route: String, val arg: List<String> = listOf()) {
    object SignUp : Screen("signup")
    object Login : Screen("login")
    object Restaurants : Screen("restaurants")
    object RestaurantDetails : Screen(route = "restaurant_details", arg = listOf("id"))
    object Cities : Screen("cities")

    fun setUpRoute(): String {
        StringBuilder().run {
            append(route)
            arg.forEach {
                append(
                    "?$it={$it}"
                )
            }
            return toString()
        }
    }
}
