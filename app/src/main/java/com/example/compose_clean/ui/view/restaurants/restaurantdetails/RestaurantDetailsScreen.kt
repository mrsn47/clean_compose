package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalDensity
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
import com.example.compose_clean.ui.composables.util.value
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.darkGray
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsViewModel.*
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private val EXPANDED_HEADER_SIZE = 200.dp
private val CONDENSED_HEADER_SIZE = 80.dp
private val BOTTOM_BAR_SIZE = 56.dp

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun RestaurantDetailsScreen(
  navController: NavController,
  restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {

  val infoState = restaurantDetailsViewModel.info.collectAsState().value
  val detailsState = restaurantDetailsViewModel.details.collectAsState().value
  val reservationDialogState = restaurantDetailsViewModel.reservationDialog.collectAsState().value
  val errorState = restaurantDetailsViewModel.error.collectAsState().value

  Content(
    infoState,
    detailsState,
    reservationDialogState,
    errorState,
    onNavigateUp = {
      navController.navigateUp()
    },
    onMenuButtonClicked = {

    },
    onSlotClicked = { timeSlot, tableNumber ->
      restaurantDetailsViewModel.sendEvent(Event.ClickSlot(timeSlot, tableNumber))
    },
    onReserveClicked = {
      restaurantDetailsViewModel.sendEvent(Event.ReserveClicked)
    },
    onReservationDoneShown = {
      restaurantDetailsViewModel.sendEvent(Event.ReservationDoneShown)
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
  infoState: InfoState,
  detailsState: DetailsState,
  reservationDialogState: ReservationDialogState,
  errorState: ErrorState,
  onNavigateUp: () -> Unit,
  onMenuButtonClicked: () -> Unit,
  onSlotClicked: (TimeSlot, String) -> Unit,
  onReserveClicked: () -> Unit,
  onReservationDoneShown: () -> Unit,
  onErrorShown: (GenericErrorMessage) -> Unit
) {

  LaunchedEffect(reservationDialogState) {
    if(reservationDialogState == ReservationDialogState.Done){
      launch {
        delay(2000)
        onReservationDoneShown()
      }
    }
  }

  val scrollState = rememberLazyListState()

  ReservationDetailsScaffold(
    errorState = errorState,
    reservationDialogState = reservationDialogState,
    onReserveClicked = { onReserveClicked() },
    onNavigateUp = { onNavigateUp() },
    onErrorShown = { onErrorShown(it) }
  ) {

    Column(modifier = Modifier.fillMaxSize()) {
      Box {
        when (detailsState) {
          is DetailsState.Loaded -> {
            Details(scrollState, detailsState.details) { timeSlot, tableNumber ->
              onSlotClicked(timeSlot, tableNumber)
            }
          }
          is DetailsState.Loading -> {
            LoadingDetails()
          }
          is DetailsState.Empty -> {
            EmptyDetails()
          }
        }
        when (infoState) {
          is InfoState.Loaded -> {
            Header(scrollState, infoState.info, onMenuButtonClicked)
          }
          InfoState.Loading -> {
            LoadingHeader()
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
      .height(EXPANDED_HEADER_SIZE)
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
  Column(
    modifier = Modifier
      .padding(top = 8.dp)
      .verticalScroll(rememberScrollState(), false)
  ) {
    Spacer(Modifier.height(EXPANDED_HEADER_SIZE))
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
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center),
    textAlign = TextAlign.Center
  )
}

@ExperimentalFoundationApi
@Composable
private fun Details(
  lazyListState: LazyListState,
  details: Details,
  onSlotClicked: (TimeSlot, String) -> Unit
) {
  LazyColumn(
    state = lazyListState,
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    item {
      Spacer(Modifier.height(EXPANDED_HEADER_SIZE))
    }
    items(details.tablesWithSlots, key = { it.table.number }) { tableWithSlots ->
      TableItem(tableWithSlots) { timeSlot ->
        onSlotClicked(timeSlot, tableWithSlots.table.number)
      }
    }
    item {
      Spacer(Modifier.height(BOTTOM_BAR_SIZE))
    }
  }
}

@Composable
private fun Header(
  scrollState: LazyListState,
  info: Info,
  menuButtonClicked: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(max(EXPANDED_HEADER_SIZE - scrollState.value(100.dp).dp, CONDENSED_HEADER_SIZE))
      .background(MaterialTheme.colors.secondary)
      .padding(horizontal = 16.dp)
  ) {
    Row {
      Image(info, scrollState)
      Info(info, scrollState, menuButtonClicked)
    }
    FadingInRestaurantName(info, scrollState)
  }
}

@Composable
private fun Image(info: Info, scrollState: LazyListState) {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .width(200.dp)
  ) {
    GlideImage(
      imageModel = info.mainImageDownloadUrl,
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
          translationX = max(-scrollState.value(100.dp.toPx()), (-60.dp.toPx()))
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
  info: Info,
  scrollState: LazyListState
) {
  Column(
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .alpha(scrollState.value(100.dp) / 50 - 2f)
      .fillMaxHeight()
  ) {
    Text(
      text = info.name,
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
  info: Info,
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
        Text(text = info.name,
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
            text = info.type,
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
          info.price?.let {
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

@Composable
private fun ReservationDetailsScaffold(
  errorState: ErrorState,
  reservationDialogState: ReservationDialogState,
  onReserveClicked: () -> Unit,
  onNavigateUp: () -> Unit,
  onErrorShown: (GenericErrorMessage) -> Unit,
  content: @Composable () -> Unit
) {

  val scaffoldState = rememberScaffoldState()
  val scope = rememberCoroutineScope()
  val density = LocalDensity.current

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      CcTopAppBar(
        backArrowClicked = { onNavigateUp() }
      )
    },
    bottomBar = {
      AnimatedVisibility(
        reservationDialogState != ReservationDialogState.NotShown,
        enter = fadeIn() + slideInVertically {
          with(density) { BOTTOM_BAR_SIZE.roundToPx() }
        },
        exit = fadeOut() + slideOutVertically {
          with(density) { BOTTOM_BAR_SIZE.roundToPx() }
        }
      ) {
        BottomAppBar(
          cutoutShape = CircleShape,
          backgroundColor = MaterialTheme.colors.secondary
        ) { }
      }
    },
    floatingActionButton = {
      AnimatedVisibility(
        reservationDialogState != ReservationDialogState.NotShown,
        enter = fadeIn() + slideInVertically {
          with(density) { BOTTOM_BAR_SIZE.roundToPx() }
        },
        exit = fadeOut() + slideOutVertically {
          with(density) { BOTTOM_BAR_SIZE.roundToPx() }
        }
      ) {
        ReservationFloatingActionButton(reservationDialogState = reservationDialogState) {
          onReserveClicked()
        }
      }
    },
    floatingActionButtonPosition = FabPosition.Center,
    isFloatingActionButtonDocked = true
  ) {
    when (errorState) {
      is ErrorState.ErrorOccurred -> {
        errorState.errorMessage.CreateSnackbar(
          scope = scope,
          scaffoldState = scaffoldState
        ) {
          onErrorShown(it)
        }
      }
      else -> {
      }
    }
    content()
  }
}

@Composable
private fun ReservationFloatingActionButton(
  reservationDialogState: ReservationDialogState,
  onReserveClicked: () -> Unit,
) {

  var text by remember { mutableStateOf("Reserve") }

  when(reservationDialogState) {
    ReservationDialogState.NotShown -> { }
    ReservationDialogState.Shown -> text = "Reserve"
    ReservationDialogState.InProgress -> text = "Reserving..."
    ReservationDialogState.Done -> text = "Done!"
  }

  ExtendedFloatingActionButton(
    text = {
      Text(text, style = Typography.button)
    },
    onClick = { onReserveClicked() },
    backgroundColor = MaterialTheme.colors.primary,
    contentColor = MaterialTheme.colors.onPrimary
  )
}

