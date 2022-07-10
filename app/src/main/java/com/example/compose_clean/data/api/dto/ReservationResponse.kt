package com.example.compose_clean.data.api.dto

import com.google.firebase.database.Exclude

data class ReservationResponse(
    @Exclude
    var id: String? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var tableNumber: String? = null
)