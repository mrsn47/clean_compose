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

    private val restaurantFlow = MutableSharedFlow<Result<RestaurantEntity>>()

    init {
        Timber.d("Initialized")
    }

    suspend fun restaurantDetails(id: String): SharedFlow<Result<RestaurantEntity>> {
        var tablesResponse: List<TableResponse> = listOf()
        var reservationsResponse: List<ReservationResponse> = listOf()
        var errorResult: String? = null
        lateinit var restaurantEntity: RestaurantEntity

        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {

            val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
                errorResult = convertToCCException(exception).userMessage
            }
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                launch {
                    restaurantEntity = dao.data(id)
                    Timber.d("Getting details from db $restaurantEntity")
                    restaurantFlow.emit(Result.DatabaseResult(restaurantEntity))
                }
                launch {
                    Timber.d("Fetching restaurant tables")
                    tablesResponse = restaurantApi.getRestaurantTables(id)
                    Timber.d("Got restaurant tables $tablesResponse")
                }
                launch {
                    Timber.d("Fetching restaurant reservations")
                    reservationsResponse = restaurantApi.getRestaurantReservations(id)
                    Timber.d("Got restaurant reservations $reservationsResponse")
                }
            }.join()
            Timber.d("Completed db update and getting from backend job")
            errorResult?.also {
                Timber.d("Emit error result $it")
                restaurantFlow.emit(Result.ErrorResult(it))
            } ?: run {
                val reservations = restaurantDetailsResponseMapper.reservationResponseListToReservations(reservationsResponse)
                val tables = restaurantDetailsResponseMapper.tableResponseListToTables(tablesResponse)
                assignReservationsToTables(reservations, tables)
                dao.updateDetails(id, tables)
                restaurantFlow.emit(
                    Result.BackendResult(dao.data(id))
                )
            }
        }

        return restaurantFlow
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