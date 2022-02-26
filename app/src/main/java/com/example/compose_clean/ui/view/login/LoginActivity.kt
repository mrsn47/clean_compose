package com.example.compose_clean.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.compose_clean.MainActivity
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.ui.theme.AppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            sessionViewModel.authState.collectLatest {
                Log.d("Auth", "Collected user in login activity $it")
                it?.let {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_NO_ANIMATION
                    )
                    startActivity(intent)
                }
            }
        }
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    // TODO: Change these without animated to not use animation api
                    val navController = rememberAnimatedNavController()
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = Screen.SignUp.route
                    ) {
                        composable(
                            route = Screen.SignUp.route,
                            popEnterTransition = {
                                slideInHorizontally(initialOffsetX = { -1000 })
                            },
                            exitTransition = { ExitTransition.None }

                        ) {
                            SignUpScreen(navController = navController, sessionViewModel)
                        }
                        composable(route = Screen.Login.route,
                            popExitTransition = { ExitTransition.None },
                            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) }
                        ) {
                            LoginScreen(navController = navController, sessionViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
    }
}