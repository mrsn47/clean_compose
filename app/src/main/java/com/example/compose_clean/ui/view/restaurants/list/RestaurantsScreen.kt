package com.example.compose_clean.ui.view.restaurants.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.view.states.ProgressState


@ExperimentalComposeUiApi
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
    val cities: List<String>? by restaurantsViewModel.cityList.observeAsState()

    Compose(restaurants,
        onSearchClicked = {
            // todo: make choice field for city
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.FilterRestaurants(
                    "Skopje",
                    it
                )
            )
        })
}

@ExperimentalComposeUiApi
@Composable
private fun Compose(restaurants: List<RestaurantEntity>?, onSearchClicked: (String) -> Unit) {

    var searchText by remember { mutableStateOf("") }
    // todo: get by shared preferences in restaurant repository
    var selectedCity by remember { mutableStateOf("Skopje") }
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = Color.White,
                elevation = 10.dp,
                actions = {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            showClearButton = it.isNotEmpty()
                        },
                        placeholder = {
                            Text(text = "Search restaurants...")
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent,
                            cursorColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
                            textColor = MaterialTheme.colors.onSecondary,
                            placeholderColor = MaterialTheme.colors.onSecondary.copy(alpha = 0.5f)
                        ),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = showClearButton,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(onClick = {
                                    showClearButton = false
                                    searchText = ""
                                    onSearchClicked("")
                                    keyboardController?.hide()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "clear",
                                        tint = MaterialTheme.colors.onSecondary
                                    )
                                }

                            }
                        },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            onSearchClicked(searchText)
                        }),
                    )
                    // todo: back press on keyboard doesn't handle this,
                    BackHandler(true) {
                        focusManager.clearFocus()
                    }
                }
            )
        }
    ) {

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
        items(restaurants, key = { it.id }) { restaurant ->
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

// Preview

@Preview
@Composable
fun ComposePreview(@PreviewParameter(MockRestaurantListProvider::class) restaurant: RestaurantEntity) {
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