package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.compose_clean.R
import com.example.compose_clean.common.GenericErrorMessage
import com.example.compose_clean.ui.composables.CcTopAppBar
import com.example.compose_clean.ui.composables.LoadingSurface
import com.example.compose_clean.ui.composables.util.CreateSnackbar
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.composables.util.value
import com.example.compose_clean.ui.theme.Shapes
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.darkGray
import com.example.compose_clean.ui.view.restaurants.list.LoadingRestaurantItem
import com.example.compose_clean.ui.view.restaurants.list.LoadingRestaurantListHeader
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsViewModel.*
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.DetailedRestaurant
import com.skydoves.landscapist.glide.GlideImage
import org.threeten.bp.ZonedDateTime
import kotlin.math.max
import kotlin.math.min

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun RestaurantDetailsScreen(
    navController: NavController,
    id: String,
    restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {

    val data = restaurantDetailsViewModel.data.collectAsState().value
    val progress = restaurantDetailsViewModel.progress.collectAsState().value

    LaunchedEffect(Unit) {
        restaurantDetailsViewModel.onInitialComposition(id)
    }

    Content(
        data,
        progress,
        navigateUp = {
            navController.navigateUp()
        },
        menuButtonClicked = {

        },
        onSlotClicked = { selectedTime, tableNumber ->
            restaurantDetailsViewModel.sendEvent(Event.ClickSlot(selectedTime, tableNumber))
        },
        onErrorShown = { errorMessage ->
            restaurantDetailsViewModel.sendEvent(
                Event.ErrorShown(errorMessage)
            )
        }
    )
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
private fun Content(
    data: DataState,
    progress: ProgressState,
    navigateUp: () -> Unit,
    menuButtonClicked: () -> Unit,
    onSlotClicked: (ZonedDateTime, String) -> Unit,
    onErrorShown: (GenericErrorMessage) -> Unit
) {
    // todo: extract constants for animations
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CcTopAppBar(
                backArrowClicked = { navigateUp() }
            )
        }
    ) {
        data.genericErrorMessage.CreateSnackbar(scope = scope, scaffoldState = scaffoldState) {
            onErrorShown(it)
        }
        when (progress) {
            is ProgressState.Loading -> {
                LoadingDetails()
                LoadingHeader()
            }
            else -> {
                if (data.detailedRestaurant != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box {
                            if (progress is ProgressState.Empty) {
                                EmptyDetails()
                            } else {
                                Details(scrollState, data.detailedRestaurant) { zdt, tableNumber ->
                                    onSlotClicked(zdt, tableNumber)
                                }
                            }
                            Header(scrollState, data.detailedRestaurant, menuButtonClicked)
                        }
                    }
                }
            }
        }

    }
}

@ExperimentalFoundationApi
@Composable
private fun LoadingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colors.secondary)
            .padding(horizontal = 16.dp)

    ) {
        LoadingImage()
    }
}

@ExperimentalFoundationApi
@Composable
private fun LoadingImage() {
    LoadingSurface(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .padding(8.dp),
        shape = CircleShape
    )
}

@ExperimentalFoundationApi
@Composable
private fun LoadingDetails() {
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 208.dp)) {
        repeat(3) {
            LoadingTableItem()
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun EmptyDetails() {
    Text(
        text = "No tables found!",
        modifier =
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center
    )
}

@ExperimentalFoundationApi
@Composable
private fun Details(lazyListState: LazyListState, detailedRestaurant: DetailedRestaurant, onSlotClicked: (ZonedDateTime, String) -> Unit) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(150.dp))
        }
        items(detailedRestaurant.tablesWithSlots, key = { it.table.number }) { tableWithSlots ->
            TableItem(tableWithSlots) {
                onSlotClicked(it, tableWithSlots.table.number)
            }
        }
    }
//    Row {
//        GlowlessOverscrollScaffold(
//            modifier = Modifier
//                .background(Color.Blue)
//                .padding(top = 50.dp)
//                .padding(horizontal = 16.dp)
//                .wrapContentHeight()
//        ) {
//            Column(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .verticalScroll(scrollState)
//            ) {
//                Spacer(Modifier.height(150.dp))
//                repeat(23) {
//                    TableItem(it.toString())
//                }
//                Spacer(Modifier.height(100.dp))
//            }
//        }
//    }
}

@Composable
private fun Header(
    scrollState: LazyListState,
    restaurant: DetailedRestaurant,
    menuButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(max(200.dp - scrollState.value(100.dp).dp, 80.dp))
            .background(MaterialTheme.colors.secondary)
            .padding(horizontal = 16.dp)
    ) {
        Row(

        ) {
            Image(restaurant, scrollState)
            Info(restaurant, scrollState, menuButtonClicked)
        }
        FadingInRestaurantName(restaurant, scrollState)
    }
}

@Composable
private fun Image(restaurant: DetailedRestaurant, scrollState: LazyListState) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
    ) {
        GlideImage(
            imageModel = restaurant.mainImageDownloadUrl,
            // Crop, Fit, Inside, FillHeight, FillWidth, None
            contentScale = ContentScale.Fit,
            // shows a placeholder while loading the image.
            loading = {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    color = MaterialTheme.colors.secondary
                )
            },
            failure = {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_restaurant_24),
                    tint = darkGray,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .graphicsLayer {
                    translationX = max(
                        -scrollState.value(100.dp.toPx()), (-60.dp.toPx())
                    )
//                                        translationY = max(-30.dp.toPx(), -scrollState.value.toFloat())
//                                        scaleX = max(0.6f, 1 - scrollState.value / 100.dp.toPx())
//                                        scaleY = max(0.6f, 1 - scrollState.value / 100.dp.toPx())
                }
                .clip(CircleShape)
        )
    }
}

@Composable
private fun FadingInRestaurantName(
    restaurant: DetailedRestaurant,
    scrollState: LazyListState
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .alpha(scrollState.value(100.dp) / 50 - 2f)
            .fillMaxHeight()
    ) {
        Text(
            text = restaurant.name,
            style = Typography.h3,
            textAlign = TextAlign.Start,
            maxLines = 1,
            modifier = Modifier
                .padding(start = 90.dp)
                .horizontalScroll(rememberScrollState()),
            color = Color.White
        )
    }
}

@Composable
private fun Info(
    restaurant: DetailedRestaurant,
    scrollState: LazyListState,
    menuButtonClicked: () -> Unit
) {
    Surface(color = MaterialTheme.colors.secondary, contentColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(1 - scrollState.value(100.dp) / 50),
            horizontalAlignment = Alignment.End
        ) {
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                // todo: make it fading edge
                Text(text = restaurant.name,
                    style = Typography.h3,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = min(scrollState.value(100.dp.toPx()), (7.sp.toPx()))
                        }
                        .horizontalScroll(rememberScrollState())
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // todo: make single line text composable
                    Text(
                        text = restaurant.type,
                        style = Typography.h4,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        )
                    )
                    IconButton(
                        onClick = { menuButtonClicked() }, modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_restaurant_menu_24),
                                contentDescription = "Menu",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier
                                    .size(42.dp)
                            )
                            Text(
                                text = "Menu",
                                style = Typography.h6,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                    restaurant.price?.let {
                        Text(
                            text = "$".repeat(it),
                            style = Typography.h3,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

