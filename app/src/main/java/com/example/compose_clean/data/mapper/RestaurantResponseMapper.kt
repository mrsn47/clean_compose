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

class RestaurantResponseMapper {

    fun restaurantResponseToEntity(data: RestaurantResponse): RestaurantEntity {
        data.let {
            return RestaurantEntity(
                id = notNullOrEmpty(it.id, "Id of RestaurantResponse is null"),
                address =  notNullOrEmpty(it.address, "Address of RestaurantResponse is null"),
                city = notNullOrEmpty(it.city, "City of RestaurantResponse is null"),
                menuUrl = it.menuUrl,
                name = notNullOrEmpty(it.name, "Name of RestaurantResponse is null"),
                price = it.price,
                type = notNullOrEmpty(it.type, "Type of RestaurantResponse is null"),
                listOf(),
                listOf(),
                null
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