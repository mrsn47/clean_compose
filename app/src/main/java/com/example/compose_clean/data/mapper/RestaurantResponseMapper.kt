package com.example.compose_clean.data.mapper

import com.example.compose_clean.common.Validator.notNull
import com.example.compose_clean.common.Validator.notNullOrEmpty
import com.example.compose_clean.common.safeCall
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity

private const val DEFAULT_TIMEZONE = "Europe/Skopje"

class RestaurantResponseMapper {

    fun restaurantResponseToEntity(data: RestaurantResponse): RestaurantEntity {
        data.let {
            return RestaurantEntity(
                id = notNullOrEmpty(it.id, "Id of RestaurantResponse is null is null or empty"),
                address =  notNullOrEmpty(it.address, "Address of RestaurantResponse is null or empty"),
                city = notNullOrEmpty(it.city, "City of RestaurantResponse is null or empty"),
                menuUrl = it.menuUrl,
                name = notNullOrEmpty(it.name, "Name of RestaurantResponse is null or empty"),
                price = it.price,
                type = notNullOrEmpty(it.type, "Type of RestaurantResponse is null or empty"),
                openingTime = notNullOrEmpty(it.openingTime, "Opening time is null or empty"),
                closingTime = notNullOrEmpty(it.closingTime, "Closing time is null or empty"),
//                zoneId = if(it.zoneId != null) ZoneId.of(it.zoneId) else ZoneId.of(DEFAULT_TIMEZONE),
                zoneId = notNullOrEmpty(it.zoneId, "Zone id is null or empty"),
                tables = listOf(),
                reservations = listOf(),
                mainImageDownloadUrl = null
            )
        }
    }

    fun restaurantResponseListToEntities(data: List<RestaurantResponse>): List<RestaurantEntity> {
        return data.mapNotNull {
            safeCall { restaurantResponseToEntity(it) }
        }
    }

    fun tableResponseToTable(data: TableResponse): Table {
        return Table(
            number = notNull(data.number, "Number of TableResponse is null"),
            type = notNull(data.type, "Number of TableResponse is null"),
            seats = notNull(data.seats, "Number of TableResponse is null")
        )
    }

    fun reservationResponseToReservation(data: ReservationResponse): Reservation {
        return Reservation(
            id = notNull(data.id, "Id of ReservationResponse is null"),
            startTime = notNull(data.startTime, "Start time of ReservationResponse is null"),
            endTime = notNull(data.endTime, "End time of ReservationResponse is null"),
            table = notNull(data.table, "Table of ReservationResponse is null")
        )
    }

    fun tableResponseListToTables(data: List<TableResponse>?): List<Table> {
        return data?.mapNotNull {
            safeCall { tableResponseToTable(it) }
        } ?: listOf()
    }

    fun reservationResponseListToReservations(data: List<ReservationResponse>?): List<Reservation> {
        return data?.mapNotNull {
            safeCall { reservationResponseToReservation(it) }
        } ?: listOf()
    }


}