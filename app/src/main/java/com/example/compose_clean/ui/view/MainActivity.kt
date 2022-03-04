package com.example.compose_clean.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.ui.theme.AppTheme
import com.example.compose_clean.ui.view.restaurants.city.CitiesScreen
import com.example.compose_clean.ui.view.restaurants.list.RestaurantsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

//    private val sessionViewModel: SessionViewModel by viewModels()
//    private val restaurantsViewModel: RestaurantsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberAnimatedNavController()
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = Screen.Restaurants.route
                    ) {
                        composable(
                            route = Screen.Restaurants.route,
                            enterTransition = { EnterTransition.None },
                            popEnterTransition = {
                                slideInHorizontally(initialOffsetX = { -1000 })
                            },
                            exitTransition = { ExitTransition.None }
                        ) {
                            RestaurantsScreen(navController)
                        }
                        composable(
                            route = Screen.Cities.route,
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                            popExitTransition = { ExitTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            CitiesScreen(navController)
                        }
                    }
                }
            }
        }
    }

    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        super.overridePendingTransition(0, 0)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
    }
}