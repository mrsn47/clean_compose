package com.example.compose_clean.nav

//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.hilt.navigation.HiltViewModelFactory
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsScreen
//import com.example.compose_clean.ui.view.restaurants.RestaurantsViewModel
//import com.example.compose_clean.ui.view.restaurants.RestaurantsScreen
//
//object EndPoints {
//    const val ID = "id"
//}
//
//@Composable
//fun NavGraph (){
//    val navController = rememberNavController()
//    val actions = remember(navController) { MainActions(navController) }
//    val context = LocalContext.current
//
//    NavHost(navController, startDestination = Screen.Restaurants.route) {
//        composable(Screen.Restaurants.route) {
//            val viewModel: RestaurantsViewModel = viewModel(
//                factory = HiltViewModelFactory(LocalContext.current, it)
//            )
//            RestaurantsScreen(restaurantsViewModel = viewModel, navController = navController)
//        }
//
//
//        composable(
//            "${Screen.Details.route}/{id}",
//            arguments = listOf(navArgument(EndPoints.ID) { type = NavType.StringType })
//        ) {
//            val viewModel = hiltViewModel<RestaurantsViewModel>(it)
//            val isbnNo = it.arguments?.getString(EndPoints.ID)
//                ?: throw IllegalStateException("'Book ISBN No' shouldn't be null")
//
//            RestaurantDetailsScreen()
//        }
//    }
//}
//
//class MainActions(navController: NavController) {
//
//    val upPress: () -> Unit = {
//        navController.navigateUp()
//    }
//
//    val gotoBookDetails: (String) -> Unit = { isbnNo ->
//        navController.navigate("${Screen.Details.route}/$isbnNo")
//    }
//
//    val gotoBookList: () -> Unit = {
//        navController.navigate(Screen.Restaurants.route)
//    }
//}