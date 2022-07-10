package com.example.compose_clean.data.api.dto

data class ReserveTableRequest(
  var startTime: Long? = null,
  var endTime: Long? = null,
  var tableNumber: String? = null
)