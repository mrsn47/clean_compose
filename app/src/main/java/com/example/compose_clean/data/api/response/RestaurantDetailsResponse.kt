package com.example.compose_clean.data.api.response

data class RestaurantDetailsResponse(
    val tables: List<TableResponse>,
    val reservations: List<ReservationResponse>
)