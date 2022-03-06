package com.example.compose_clean.ui.view.restaurants.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.compose_clean.ui.composables.util.CreateSnackbar
import com.example.compose_clean.ui.composables.util.noRippleClickable
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.view.states.GenericError


@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun RestaurantsScreen(
    navController: NavController,
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel()
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var searchText: String by rememberSaveable { mutableStateOf("") }
    var showClearButton by rememberSaveable { mutableStateOf(false) }

    val data = restaurantsViewModel.data.collectAsState().value
    val state = restaurantsViewModel.state.collectAsState().value

    // TODO: make this not trigger on config change, make this send event
    LaunchedEffect(Unit) {
        restaurantsViewModel.launchedEffect(searchText)
    }

    Content(
        data,
        state,
        searchText,
        showClearButton,
        onSearchClicked = {
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.FilterRestaurants(
                    data.selectedCity,
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
            searchText = text
            showClearButton = text.isNotEmpty()
        },
        onClearSearchClicked = {
            searchText = ""
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.FilterRestaurants(
                    data.selectedCity,
                    ""
                )
            )
            showClearButton = false
        },
        onItemClicked = { id ->
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.NavigateToDetails(navController, id)
            )
        }
    )
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun Content(
    data: RestaurantsViewModel.UiData,
    progress: RestaurantsViewModel.UiProgress,
    searchText: String,
    showClearButton: Boolean,
    onSearchClicked: (String) -> Unit = { },
    onChangeCityClicked: () -> Unit = { },
    onSearchValueChange: (String) -> Unit = { },
    onClearSearchClicked: () -> Unit = { },
    onItemClicked: (String) -> Unit = { }
) {

    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
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
                            onSearchValueChange(it)
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
                                    onClearSearchClicked()
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
                            onSearchClicked(searchText)
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
        data.genericError.CreateSnackbar(scope = scope, scaffoldState = scaffoldState)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box() {
                Row() {
                    data.groupedRestaurants?.let {
                        RestaurantList(it) {
                            onItemClicked(it)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(vertical = 8.spToDp(), horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top
                ) {
                    data.selectedCity?.let {
                        DrawableWrapper(
                            modifier = Modifier.noRippleClickable {
                                onChangeCityClicked()
                            },
                            drawableEnd = R.drawable.ic_baseline_keyboard_arrow_right_24
                        ) {
                            Text(
                                it,
                                style = Typography.h4,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun RestaurantList(
    groupedRestaurants: Map<String, List<RestaurantEntity>>,
    onItemClicked: (String) -> Unit
) {

    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        groupedRestaurants.forEach { (type, restaurants) ->
            stickyHeader {
                RestaurantListHeader(type = type)
            }
            items(restaurants, key = { it.id }) {
                RestaurantItem(
                    restaurant = it,
                    onItemClicked = {
                        onItemClicked(it)
                    }
                )
            }
        }
    }
}

// Preview

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun ComposePreview(@PreviewParameter(MockRestaurantListProvider::class) restaurant: RestaurantEntity) {
    Content(
        RestaurantsViewModel.UiData(mapOf("Mexican" to listOf(restaurant)), "City", GenericError("Some restaurant")),
        RestaurantsViewModel.UiProgress.LoadedProgressState,
        "search text",
        true
    )
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