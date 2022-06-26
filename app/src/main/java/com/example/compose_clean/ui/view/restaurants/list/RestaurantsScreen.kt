package com.example.compose_clean.ui.view.restaurants.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.compose_clean.R
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.ui.composables.CcTopAppBar
import com.example.compose_clean.ui.composables.DrawableWrapper
import com.example.compose_clean.ui.composables.util.CreateSnackbar
import com.example.compose_clean.ui.composables.util.noRippleClickable
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.theme.Shapes
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.gray
import com.example.compose_clean.ui.view.restaurants.list.RestaurantsViewModel.UiData
import com.example.compose_clean.ui.view.restaurants.list.RestaurantsViewModel.UiProgress
import com.example.compose_clean.common.GenericErrorMessage


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
    val progress = restaurantsViewModel.progress.collectAsState().value

    // TODO: make this not trigger on config change
    LaunchedEffect(Unit) {
        restaurantsViewModel.launchedEffect(searchText)
    }

    Content(
        data,
        progress,
        TopAppBarActions(
            searchText,
            showClearButton,
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
            onSearchClicked = {
                restaurantsViewModel.sendEvent(
                    RestaurantsViewModel.Event.FilterRestaurants(
                        data.selectedCity,
                        it
                    )
                )
                keyboardController?.hide()
            },
        ),
        onChangeCityClicked = {
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.NavigateToChangeCity(navController)
            )
        },
        onItemClicked = { id ->
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.NavigateToDetails(navController, id)
            )
        },
        onErrorShown = { errorMessage ->
            restaurantsViewModel.sendEvent(
                RestaurantsViewModel.Event.ErrorShown(errorMessage)
            )
        }
    )
}

private data class TopAppBarActions(
    val searchText: String = "",
    val showClearButton: Boolean = false,
    val onSearchValueChange: (String) -> Unit = {},
    val onClearSearchClicked: () -> Unit = {},
    val onSearchClicked: (String) -> Unit = {}
)

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun Content(
    data: UiData,
    progress: UiProgress,
    topAppBarActions: TopAppBarActions,
    onChangeCityClicked: () -> Unit = { },
    onItemClicked: (String) -> Unit = { },
    onErrorShown: (GenericErrorMessage) -> Unit = { }
) {

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CcTopAppBar(
                actions = {
                    SearchTextField(topAppBarActions)
                }
            )
        },
        backgroundColor = MaterialTheme.colors.surface
    ) {
        data.genericErrorMessage.CreateSnackbar(scope = scope, scaffoldState = scaffoldState) {
            onErrorShown(it)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box {
                Row {
                    when (progress) {
                        is UiProgress.LoadingProgressState -> {
                            LoadingRestaurantList()
                        }
                        else -> {
                            if (!data.groupedRestaurants.isNullOrEmpty()) {
                                if (progress is UiProgress.EmptyProgressState) {
                                    EmptyResult()
                                } else {
                                    RestaurantList(data.groupedRestaurants) {
                                        onItemClicked(it)
                                    }
                                }
                            }
                        }
                    }
                }
                TopCityBar(data, onChangeCityClicked)
            }
        }
    }
}

@Composable
private fun EmptyResult() {
    Text(
        text = "No restaurants found!",
        modifier =
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TopCityBar(data: UiData, onChangeCityClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(vertical = 6.dp)
                .padding(vertical = 8.spToDp(), horizontal = 16.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val color by infiniteTransition.animateColor(
                initialValue = Color.LightGray, targetValue = gray,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            if (data.selectedCity != null) {
                DrawableWrapper(
                    modifier = Modifier.noRippleClickable {
                        onChangeCityClicked()
                    },
                    drawableEnd = R.drawable.ic_baseline_keyboard_arrow_right_24
                ) {
                    Text(
                        data.selectedCity,
                        style = Typography.h4,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }
            } else {
                Surface(
                    color = color,
                    modifier = Modifier
                        .height(24.spToDp())
                        .width(75.spToDp()),
                    shape = Shapes.small
                ) {}
            }
        }
    }
}

@Composable
private fun RowScope.SearchTextField(
    actions: TopAppBarActions,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = actions.searchText,
        onValueChange = {
            actions.onSearchValueChange(it)
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
                visible = actions.showClearButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = {
                    actions.onClearSearchClicked()
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
            actions.onSearchClicked(actions.searchText)
            focusManager.clearFocus()
        }),
    )
    // todo: back press on keyboard doesn't handle this,
    BackHandler(true) {
        focusManager.clearFocus()
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
            items(restaurants, key = { it.id }) { restaurant ->
                RestaurantItem(
                    restaurant = restaurant,
                    onItemClicked = {
                        onItemClicked(it)
                    }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun LoadingRestaurantList(
) {
    Column {
        LoadingRestaurantListHeader()
        repeat(10) {
            LoadingRestaurantItem()
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
        UiData(mapOf("Mexican" to listOf(restaurant)), "City", GenericErrorMessage("Some restaurant")),
        UiProgress.LoadedProgressState,
        TopAppBarActions()
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
            "08:00",
            "08:00",
            "Europe/Skopje",
            listOf(),
            null
        )
    )
    override val count: Int = values.count()
}