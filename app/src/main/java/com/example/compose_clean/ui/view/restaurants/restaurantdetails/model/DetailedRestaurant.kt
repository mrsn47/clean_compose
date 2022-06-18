package com.example.compose_clean.ui.view.restaurants.restaurantdetails.model

class DetailedRestaurant(
    val address: String,
    val menuUrl: String?,
    val name: String,
    val price: Int?,
    val type: String,
    val tablesWithSlots: List<TableWithSlots>,
    val mainImageDownloadUrl: String?
)