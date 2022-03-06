package com.example.compose_clean.data.api.response

import com.google.firebase.database.Exclude

data class TableResponse(
    @Exclude
    var number: String? = null,
    var type: String? = null,
    var seats: Int? = null
)