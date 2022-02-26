package com.example.compose_clean.ui.view.restaurants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.view.states.ProgressState

@Composable
fun RestaurantsScreen(
    navController: NavController,
    restaurantsViewModel: RestaurantsViewModel
) {

    val state = restaurantsViewModel.state.collectAsState()
    when (state.value) {
        is ProgressState.LoadingProgressState -> {

        }
        is ProgressState.LoadedProgressState -> {

        }
        is ProgressState.EmptyProgressState -> {

        }
    }

    val restaurants: List<RestaurantEntity>? by restaurantsViewModel.restaurants.observeAsState()
    restaurants?.let { RestaurantList(it) }
}

@Composable
fun RestaurantList(restaurants: List<RestaurantEntity>) {
    Scaffold(
       modifier = Modifier
           .fillMaxSize()
           .padding(8.dp)
           .background(
           brush = Brush.verticalGradient(
               colors = listOf(
                   Color(0xFFFFFFFF),
                   Color(0xFFE4E4E4)
               )
           )
       )
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colors.background
                )
        ) {
            items(restaurants) { restaurant ->
                RestaurantItem(
                    restaurant = restaurant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                        }

                )
            }
        }
    }

}