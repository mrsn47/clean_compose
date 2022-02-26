package com.example.compose_clean.ui.view.restaurants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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

    Compose(restaurants)
}

@Composable
private fun Compose(restaurants: List<RestaurantEntity>?) {
    Scaffold {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD6D6D6),
                            Color(0xFFF5F5F5),
                        )
                    )
                )
        ) {
            restaurants?.let { RestaurantList(it) }
        }
    }
}

@Composable
fun RestaurantList(restaurants: List<RestaurantEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp)
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

@Preview
@Composable
fun ComposePreview(@PreviewParameter(MockRestaurantListProvider::class) restaurant: RestaurantEntity) {
    RestaurantList(listOf(restaurant))
}

@Preview
@Composable
fun RestaurantListPreview(@PreviewParameter(MockRestaurantListProvider::class) restaurant: RestaurantEntity) {
    RestaurantList(listOf(restaurant))
}

class MockRestaurantListProvider : PreviewParameterProvider<RestaurantEntity> {
    override val values = sequenceOf(
        RestaurantEntity(
            "",
            "Partizanski Odredi",
            "Skopje",
            "",
            "Mock Restaurant",
            2,
            "Mexican",
            null,
            null,
            null
        )
    )
    override val count: Int = values.count()
}