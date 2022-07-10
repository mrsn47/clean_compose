package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose_clean.R
import com.example.compose_clean.ui.composables.LoadingSurface
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.theme.Shapes
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.black
import com.example.compose_clean.ui.theme.darkGray
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TableWithSlots
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import org.threeten.bp.ZonedDateTime

@Composable
fun TableItem(tableWithSlots: TableWithSlots, onSlotClicked: (TimeSlot) -> Unit) {

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column {
            Row(Modifier.wrapContentHeight().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                TableName(tableWithSlots.table.type)
                TableImage(tableWithSlots.table.type)
            }
            Row {
                TimeSlots(tableWithSlots.slots) {
                    onSlotClicked(it)
                }
            }
        }
    }
    Divider(color = darkGray, thickness = 1.dp)
}

@Composable
fun TableName(type: String) {
    Text(text = type, style = Typography.h5)
}

@Composable
fun TableImage(type: String) {
    // todo: use different image based on type
    Icon(
        painter = painterResource(R.drawable.ic_baseline_restaurant_24),
        tint = black,
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .padding(start = 8.dp)
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeSlots(slots: List<TimeSlot>, onSlotClicked: (TimeSlot) -> Unit) {
    LazyRow(
        modifier = Modifier
            .padding(top = 20.dp)
    ) {
        item {
            Spacer(Modifier.width(16.dp))
        }
        items(slots, key = { it.zonedDateTime }) { slot ->
            SlotItem(slot, Modifier.animateItemPlacement(
                spring(

                )
            )) {
                onSlotClicked(it)
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun LoadingTableItem() {
    Column(modifier = Modifier.padding(8.dp)) {
        LoadingSurface(
            modifier = Modifier
                .height(36.spToDp())
                .width(200.spToDp()),
            shape = Shapes.small
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState(), false)

        ) {
            repeat(8) {
                LoadingSlotItem()
            }
        }
    }
}
