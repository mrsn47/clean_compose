package com.example.compose_clean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.example.compose_clean.common.model.Reservation
import com.example.compose_clean.common.model.Table

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
    val tables: List<Table>?,
    val reservations: List<Reservation>?,
    var mainImageDownloadUrl: String?
)