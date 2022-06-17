package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.compose_clean.ui.composables.CcTopAppBar
import com.example.compose_clean.ui.composables.LoadingSurface
import com.example.compose_clean.ui.composables.util.CreateSnackbar
import com.example.compose_clean.ui.composables.util.GlowlessOverscrollScaffold
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.darkGray
import com.example.compose_clean.ui.view.restaurants.list.LoadingRestaurantItem
import com.example.compose_clean.ui.view.restaurants.list.LoadingRestaurantListHeader
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsViewModel.*
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.DetailedRestaurant
import com.skydoves.landscapist.glide.GlideImage
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
        restaurantDetailsViewModel.launchedEffect(id)
    }

    Content(
        data,
        progress,
        navigateUp = {
            navController.navigateUp()
        },
        menuButtonClicked = {
            navController.navigateUp()
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
    menuButtonClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CcTopAppBar(
                backArrowClicked = { navigateUp() }
            )
        }
    ) {
        data.genericError.CreateSnackbar(scope = scope, scaffoldState = scaffoldState)
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
                                Details(scrollState)
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
    Row {
        Column {
            LoadingRestaurantListHeader()
            repeat(10) {
                LoadingRestaurantItem()
            }
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
private fun Details(scrollState: ScrollState) {
    Row {
        GlowlessOverscrollScaffold(
            modifier = Modifier
                .padding(top = 50.dp)
                .verticalScroll(scrollState)
        ) {
            Column {
                Spacer(modifier = Modifier.height(150.dp))
                repeat(23) {
                    Text(
                        modifier = Modifier.padding(20.dp),
                        text = "TesasadasdtaTesasadasdtaTesasadasdtaTesasadasdtaTesasadasdta"
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    scrollState: ScrollState,
    restaurant: DetailedRestaurant,
    menuButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(max(200.dp - scrollState.value.dp, 80.dp))
            .background(MaterialTheme.colors.secondary)
            .padding(horizontal = 16.dp)

    ) {
        Image(restaurant, scrollState)
        Info(restaurant, scrollState, menuButtonClicked)
    }
}

@Composable
private fun Image(restaurant: DetailedRestaurant, scrollState: ScrollState) {
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
                    translationX = max(-scrollState.value.toFloat(), (-60.dp.toPx()))
//                                        translationY = max(-30.dp.toPx(), -scrollState.value.toFloat())
//                                        scaleX = max(0.6f, 1 - scrollState.value / 100.dp.toPx())
//                                        scaleY = max(0.6f, 1 - scrollState.value / 100.dp.toPx())
                }
                .clip(CircleShape)
        )
    }
}

@Composable
private fun Info(
    restaurant: DetailedRestaurant,
    scrollState: ScrollState,
    menuButtonClicked: () -> Unit
) {
    // todo: add changeable by scroll alpha for lines after 1st on title
    Surface(color = MaterialTheme.colors.secondary, contentColor = Color.White) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(text = restaurant.name, style = Typography.h3, textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = min(scrollState.value.toFloat(), (7.sp.toPx()))
                        })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(1 - scrollState.value.toFloat() / 50),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = restaurant.type, style = Typography.h4, textAlign = TextAlign.Center)
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
                        Text(text = "$".repeat(it), style = Typography.h3, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

