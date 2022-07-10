package com.example.compose_clean.ui.view.restaurants.restaurantdetails.model

import org.threeten.bp.ZonedDateTime

data class TimeSlot(
    val zonedDateTime: ZonedDateTime,
    val reservationOwner: ReservationOwner = ReservationOwner.NOT_RESERVED,
    val selected: Boolean
)

enum class ReservationOwner {
    CURRENT_USER, OTHER, NOT_RESERVED
}