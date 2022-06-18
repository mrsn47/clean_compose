package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.*
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantDetailsResponseMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class RestaurantDetailsRepository @Inject constructor(
    private val restaurantDetailsResponseMapper: RestaurantDetailsResponseMapper,
    private val dao: RestaurantDao,
    private val restaurantApi: RestaurantApi
) {

    private val _restaurant = MutableSharedFlow<Result<RestaurantEntity>>(replay = 1)
    val restaurant: SharedFlow<Result<RestaurantEntity>> = _restaurant

    init {
        Timber.d("Initialized")
    }

    suspend fun restaurantDetails(id: String): SharedFlow<Result<RestaurantEntity>> {
        var tablesResponse: List<TableResponse> = listOf()
        var reservationsResponse: List<ReservationResponse> = listOf()
        lateinit var restaurantEntity: RestaurantEntity

        val dbUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            restaurantEntity = dao.data(id)
            _restaurant.emit(Result.DatabaseResult(restaurantEntity))
        }
        val result = safeResultWithContext(Dispatchers.IO) {
            it.launch {
                Timber.d("Fetching restaurant tables")
                tablesResponse = restaurantApi.getRestaurantTables(id)
                Timber.d("Got restaurant tables $tablesResponse")
            }
            it.launch {
                Timber.d("Fetching restaurant reservations")
                reservationsResponse = restaurantApi.getRestaurantReservations(id)
                Timber.d("Got restaurant reservations $reservationsResponse")
            }
        }

        dbUpdateJob.join()

        if(result.error != null) {
            _restaurant.emit(Result.ErrorResult(result.error))
        } else {
            val reservations = restaurantDetailsResponseMapper.reservationResponseListToReservations(reservationsResponse)
            val tables = restaurantDetailsResponseMapper.tableResponseListToTables(tablesResponse)
            assignReservationsToTables(reservations, tables)
            dao.updateDetails(id, tables)
            _restaurant.emit(
                Result.BackendResult(dao.data(id))
            )
        }
        return restaurant
    }

    private fun assignReservationsToTables(reservations: List<Reservation>, tables: List<Table>) {
        tables.forEach { table ->
            val reservationList = arrayListOf<Reservation>()
            reservations.forEach { reservation ->
                if(table.number == reservation.tableNumber) {
                    reservationList.add(reservation)
                }
            }
            table.reservations = reservationList
        }
    }

}