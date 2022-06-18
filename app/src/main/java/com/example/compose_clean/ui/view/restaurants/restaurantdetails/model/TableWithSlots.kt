package com.example.compose_clean.ui.view.restaurants.restaurantdetails.model

import com.example.compose_clean.data.db.model.Table

data class TableWithSlots (
    val table: Table,
    val slots: List<TimeSlot>
)

