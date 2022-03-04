package com.example.compose_clean.ui.view.restaurants.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.compose_clean.R
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.composables.DrawableWrapper
import com.example.compose_clean.ui.composables.util.noRippleClickable
import com.example.compose_clean.ui.theme.Typography


@ExperimentalComposeUiApi
@Composable
fun RestaurantsScreen(
    navController: NavController,
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel()
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    // todo: remembersaveable, pass the state to Content()
    var restaurants: List<RestaurantEntity> by remember { mutableStateOf(listOf()) }
    var selectedCity: String? by rememberSaveable { mutableStateOf(null) }
    var searchInput: String by rememberSaveable { mutableStateOf("") }
    var showClearButton by rememberSaveable { mutableStateOf(false) }

    val state = restaurantsViewModel.state.collectAsState()
    state.value.run {
        when (this) {
            is RestaurantsViewModel.UiState.LoadingProgressState -> {

            }
            is RestaurantsViewModel.UiState.LoadedProgressState -> {

            }
            is RestaurantsViewModel.UiState.EmptyProgressState -> {

            }
            is RestaurantsViewModel.UiState.Data -> {
                restaurantData?.let { restaurants = it }
                selectedCityData?.let {
                    selectedCity = it
                }
            }
            is RestaurantsViewModel.UiState.GenericError -> {

            }
        }

    }

    // TODO: make this not trigger on config change
    LaunchedEffect(Unit) {
        restaurantsViewModel.launchedEffect(searchInput)
    }

    Content(restaurants,
        selectedCity,
        searchInput,
        showClearButton,
        onSearchClicked = {
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.FilterRestaurants(
                    selectedCity,
                    it
                )
            )
            keyboardController?.hide()
        },
        onChangeCityClicked = {
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.NavigateToChangeCity(navController)
            )
        },
        onSearchValueChange = { text ->
            searchInput = text
            showClearButton = text.isNotEmpty()
        },
        onClearSearchClicked = {
            searchInput = ""
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.FilterRestaurants(
                    selectedCity,
                    ""
                )
            )
            showClearButton = false
        }
    )
}

@ExperimentalComposeUiApi
@Composable
private fun Content(
    restaurants: List<RestaurantEntity>?,
    city: String?,
    searchText: String,
    showClearButton: Boolean,
    onSearchClicked: ((String) -> Unit)? = null,
    onChangeCityClicked: (() -> Unit)? = null,
    onSearchValueChange: ((String) -> Unit)? = null,
    onClearSearchClicked: (() -> Unit)? = null
) {

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
                            onSearchValueChange?.invoke(it)
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
                                    onClearSearchClicked?.invoke()
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
                            onSearchClicked?.invoke(searchText)
                            focusManager.clearFocus()
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
        Column(
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                city?.let {
                    DrawableWrapper(
                        modifier = Modifier.noRippleClickable {
                            onChangeCityClicked?.invoke()
                        },
                        drawableEnd = R.drawable.ic_baseline_keyboard_arrow_right_24
                    ) {
                        Text(
                            city,
                            style = Typography.h4,
                            modifier = Modifier
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
            Row() {
                restaurants?.let { RestaurantList(it) }
            }
        }
    }
}

@Composable
fun RestaurantList(restaurants: List<RestaurantEntity>) {

    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(restaurants, key = { it.id }) { restaurant ->
            RestaurantItem(
                restaurant = restaurant
            )
        }
    }
}

// Preview

@ExperimentalComposeUiApi
@Preview
@Composable
fun ComposePreview(@PreviewParameter(MockRestaurantListProvider::class) restaurant: RestaurantEntity) {
    Content(listOf(restaurant), "City", "Some restaurant", true)
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