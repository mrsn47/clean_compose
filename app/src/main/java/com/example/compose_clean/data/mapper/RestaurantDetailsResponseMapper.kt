package com.example.compose_clean.data.mapper

import com.example.compose_clean.common.Validator
import com.example.compose_clean.common.safeCall
import com.example.compose_clean.data.api.dto.ReservationResponse
import com.example.compose_clean.data.api.dto.TableResponse
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class RestaurantDetailsResponseMapper {

    fun tableResponseListToTables(data: List<TableResponse>?): List<Table> {
        return data?.mapNotNull {
            safeCall { tableResponseToTable(it) }
        } ?: listOf()
    }

    fun reservationResponseListToReservations(data: List<ReservationResponse>?): List<Reservation> {
        val reservationList = ArrayList<Reservation>()
        data?.forEach {
            val reservation = safeCall { reservationResponseToReservation(it) }
            if (reservation != null) {
                reservationList.addAll(reservation)
            }
        }
        return reservationList
    }

    fun tableResponseToTable(data: TableResponse): Table {
        return Table(
            number = Validator.notNullOrEmpty(data.number, "Number of TableResponse is null"),
            type = Validator.notNullOrEmpty(data.type, "Type of TableResponse is null"),
            seats = Validator.notNull(data.seats, "Seats of TableResponse is null")
        )
    }

    fun reservationResponseToReservation(data: ReservationResponse): List<Reservation> {
        val id = Validator.notNull(data.id, "Id of ReservationResponse is null")
        val startTime = Validator.notNull(data.startTime, "Start time of ReservationResponse is null")
        val endTime = Validator.notNull(data.endTime, "End time of ReservationResponse is null")
        val tableNumber = Validator.notNull(data.tableNumber, "Table Number of ReservationResponse is null")
        return reservationListFromResponse(id, startTime, endTime, tableNumber)
    }

    private fun reservationListFromResponse(id: String, startTime: Long, endTime: Long, tableNumber: String): List<Reservation> {
        var startTimeInstant = Instant.ofEpochSecond(startTime)
        val endTimeInstant = Instant.ofEpochSecond(endTime)
        val reservation = ArrayList<Reservation>()
        while(startTimeInstant.isBefore(endTimeInstant)){
            reservation.add(
                Reservation(id, startTimeInstant.epochSecond, tableNumber)
            )
            startTimeInstant = startTimeInstant.plus(30, ChronoUnit.MINUTES)
        }
        return reservation
    }

}