package com.example.compose_clean.data.api.dto

import com.google.firebase.database.Exclude

data class RestaurantDto(
    @Exclude
    var id: String? = null,
    val address: String? = null,
    val city: String? = null,
    val menuUrl: String? = null,
    val name: String? = null,
    val price: Int? = null,
    val type: String? = null,
)