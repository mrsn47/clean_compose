package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import com.example.compose_clean.common.formatForUi
import com.example.compose_clean.ui.composables.LoadingSurface
import com.example.compose_clean.ui.composables.util.spToDp
import com.example.compose_clean.ui.theme.*
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.ReservationOwner
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import org.threeten.bp.ZonedDateTime

@Composable
fun SlotItem(slot: TimeSlot, modifier: Modifier = Modifier, onSlotClicked: (TimeSlot) -> Unit) {

  val (buttonBackgroundColor, buttonContentColor, clickable) = when (slot.reservationOwner) {
    ReservationOwner.CURRENT_USER -> Triple(Color.DarkGray, Color.Black, true)
    ReservationOwner.OTHER -> Triple(MaterialTheme.colors.surface, MaterialTheme.colors.onSurface, false)
    ReservationOwner.NOT_RESERVED -> {
      if (slot.selected) {
        Triple(MaterialTheme.colors.primary, MaterialTheme.colors.onPrimary, true)
      } else {
        Triple(MaterialTheme.colors.surface, MaterialTheme.colors.onSurface, true)
      }
    }
  }

  OutlinedButton(
    enabled = clickable,
    colors = ButtonDefaults.outlinedButtonColors(
      backgroundColor = buttonBackgroundColor,
      contentColor = buttonContentColor
    ),
    contentPadding = PaddingValues(10.dp),
    border = BorderStroke(2.dp, MaterialTheme.colors.primary).takeIf { !slot.selected && clickable },
    shape = RoundedCornerShape(20.dp),
    modifier = modifier
      .requiredSizeIn(minWidth = 50.dp, minHeight = 40.dp),
    onClick = {
      onSlotClicked(slot)
    }
  ) {
    Text(slot.zonedDateTime.formatForUi(), style = Typography.button, fontWeight = SemiBold.takeIf { clickable })
  }


//    Surface(
//        color = buttonBackgroundColor,
//        contentColor = white,
//        elevation = 2.dp,
//        modifier = Modifier
//            .requiredSizeIn(minWidth = 50.dp, minHeight = 40.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .border(BorderStroke(2.dp, MaterialTheme.colors.primary))
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Bottom,
//            modifier = Modifier.clickable(enabled = clickable) { onSlotClicked(slot) },
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(slot.zonedDateTime.formatForUi(), style = Typography.h4)
//        }
//    }

}

@Composable
fun LoadingSlotItem() {
  LoadingSurface(
    modifier = Modifier
      .padding(top = 20.dp)
      .padding(horizontal = 4.dp)
      .height(40.dp)
      .width(60.spToDp()),
    shape = Shapes.small
  )
}