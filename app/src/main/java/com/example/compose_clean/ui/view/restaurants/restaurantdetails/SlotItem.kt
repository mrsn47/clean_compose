package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose_clean.common.formatForUi
import com.example.compose_clean.ui.theme.*
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.ReservationOwner
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import org.threeten.bp.ZonedDateTime

@Composable
fun SlotItem(slot: TimeSlot, onSlotClicked: (ZonedDateTime) -> Unit) {

    val (buttonBackgroundColor, clickable) = when(slot.reservationOwner) {
        ReservationOwner.CURRENT_USER -> Pair(Color.DarkGray, true)
        ReservationOwner.OTHER -> Pair(MaterialTheme.colors.secondary, false)
        ReservationOwner.NOT_RESERVED -> Pair(MaterialTheme.colors.primary, true)
        ReservationOwner.SELECTED -> Pair(bronze, true)
    }

    Surface(
        color = buttonBackgroundColor,
        contentColor = white,
        elevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .requiredSizeIn(minWidth = 50.dp, minHeight = 40.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.clickable(enabled = clickable) { onSlotClicked(slot.zonedDateTime) }.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(slot.zonedDateTime.formatForUi(), style = Typography.h4)
        }
    }

}