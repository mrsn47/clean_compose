package com.example.compose_clean.data.db.model

data class Reservation(
    val id: String,
    val startTime: Long,
    val tableNumber: String
)