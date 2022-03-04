package com.example.compose_clean.ui.view.restaurants.city

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@ExperimentalComposeUiApi
@Composable
fun CitiesScreen(
    navController: NavController,
    cityViewModel: CityViewModel = hiltViewModel()
) {

    val cities: List<String>? by cityViewModel.cities.observeAsState()


    Compose(cities,
        onCityClicked = {
            cityViewModel.navigateToRestaurantsScreen(navController, it)
        }
    )


}

@ExperimentalComposeUiApi
@Composable
private fun Compose(cities: List<String>?, onCityClicked: (String) -> Unit) {

    cities?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(cities, key = { it }) { city ->
                CityItem(
                    city = city,
                ) {
                    onCityClicked(it)
                }
            }
        }
    }
}