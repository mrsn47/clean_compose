package com.example.compose_clean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey
    val id: String,
    val address: String,
    val city: String,
    val menuUrl: String,
    val name: String,
    val price: Int,
    val type: String,
    var tables: List<TableResponse>?,
    var reservations: List<ReservationResponse>?,
    var mainImageDownloadUrl: String?
)