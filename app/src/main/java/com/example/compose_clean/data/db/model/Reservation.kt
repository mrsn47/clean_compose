package com.example.compose_clean.data.db.model

import com.example.compose_clean.data.api.response.TableResponse

data class Reservation(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val table: TableResponse
)