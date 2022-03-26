package com.example.compose_clean.ui.view.restaurants.restaurantdetails.model

import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table

data class Details(
    val tables: List<Table>,
    val reservations: List<Reservation>
)