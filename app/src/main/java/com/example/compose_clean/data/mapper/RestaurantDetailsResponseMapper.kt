package com.example.compose_clean.data.mapper

import com.example.compose_clean.common.Validator
import com.example.compose_clean.common.safeCall
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table

class RestaurantDetailsResponseMapper {

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

    fun tableResponseToTable(data: TableResponse): Table {
        return Table(
            number = Validator.notNullOrEmpty(data.number, "Number of TableResponse is null"),
            type = Validator.notNullOrEmpty(data.type, "Type of TableResponse is null"),
            seats = Validator.notNull(data.seats, "Seats of TableResponse is null")
        )
    }

    fun reservationResponseToReservation(data: ReservationResponse): Reservation {
        return Reservation(
            id = Validator.notNull(data.id, "Id of ReservationResponse is null"),
            startTime = Validator.notNull(
                data.startTime,
                "Start time of ReservationResponse is null"
            ),
            endTime = Validator.notNull(data.endTime, "End time of ReservationResponse is null"),
            tableNumber = Validator.notNull(data.tableNumber, "Table Number of ReservationResponse is null")
        )
    }
}