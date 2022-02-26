package com.example.compose_clean.common.model

data class Reservation(
    val startTime: Long,
    val endTime: Long,
    val table: Table
)