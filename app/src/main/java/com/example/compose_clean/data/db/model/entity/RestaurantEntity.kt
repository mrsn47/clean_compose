package com.example.compose_clean.data.db.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey
    val id: String,
    val address: String,
    val city: String,
    val menuUrl: String?,
    val name: String,
    val price: Int?,
    val type: String,
    val openingTime: String,
    val closingTime: String,
    val zoneId: String,
    var tables: List<Table>,
    var mainImageDownloadUrl: String?
)