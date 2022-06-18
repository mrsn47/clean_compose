package com.example.compose_clean.data.db.model

data class Table(
    val number: String,
    val type: String,
    val seats: Int,
    var reservations: List<Reservation> = listOf()
)