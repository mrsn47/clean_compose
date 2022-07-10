package com.example.compose_clean.data.repository

import com.example.compose_clean.common.*
import com.example.compose_clean.data.ConnectivityService
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.api.dto.ReservationResponse
import com.example.compose_clean.data.api.dto.TableResponse
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantDetailsResponseMapper
import com.example.compose_clean.domain.repository.RestaurantDetailsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

class RestaurantDetailsRepositoryImpl @Inject constructor(
  private val connectivityService: ConnectivityService,
  private val restaurantDetailsResponseMapper: RestaurantDetailsResponseMapper,
  private val dao: RestaurantDao,
  private val restaurantApi: RestaurantApi,
  private val externalScope: CoroutineScope,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RestaurantDetailsRepository {

  private val restaurantFlow = MutableSharedFlow<Result<RestaurantEntity>>()

  init {
    Timber.d("Initialized")
  }

  override suspend fun restaurantDetails(id: String): SharedFlow<Result<RestaurantEntity>> {
    emitInBackground(id)
    Timber.d("Returning flow")
    return restaurantFlow
  }

  override suspend fun refresh(id: String) {
    Timber.d("Refreshing")
    emitInBackground(id)
  }

  override suspend fun reserveTable(
    restaurantId: String,
    tableNumber: String,
    startTime: ZonedDateTime,
    endTime: ZonedDateTime
  ): GenericResult<Unit> = safeResultWithContext(Dispatchers.IO) {
    if (connectivityService.hasNoInternetConnection()) {
      restaurantFlow.emit(Result.ErrorResult("Could not reach the server. Check your internet connection"))
      throw CCException("Could not reach the server. Check your internet connection")
    }
    restaurantApi.reserveTable(restaurantId, tableNumber, startTime.toEpochSecond(), endTime.toEpochSecond())
  }

  private suspend fun emitInBackground(id: String) {
    externalScope.launch(dispatcher) {
      getFromDatabaseAndEmit(id)
    }
    externalScope.launch(dispatcher) {
      getFromBackendAndEmit(id)
    }
    Timber.d("Launched background coroutines")
  }

  private suspend fun getFromBackendAndEmit(id: String) {
    if (connectivityService.hasNoInternetConnection()) {
      restaurantFlow.emit(Result.ErrorResult("Could not reach the server. Check your internet connection"))
      return
    }

    Timber.d("Getting from backend")
    var tablesResponse: List<TableResponse> = listOf()
    var reservationsResponse: List<ReservationResponse> = listOf()
    var errorResult: String? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
      val ccException = convertToCCException(exception)
      Timber.e("Caught exception in coroutine", ccException)
      Timber.e(exception)
      errorResult = ccException.userMessage
    }
    externalScope.launch(coroutineExceptionHandler) {
      Timber.d("Fetching restaurant tables")
      val tablesFuture = async { restaurantApi.getRestaurantTables(id) }
      Timber.d("Fetching restaurant reservations")
      val reservationsFuture = async { restaurantApi.getRestaurantReservations(id) }
      tablesResponse = tablesFuture.await()
      Timber.d("Got restaurant tables $tablesResponse")
      reservationsResponse = reservationsFuture.await()
      Timber.d("Got restaurant reservations $reservationsResponse")
    }.join()

    Timber.d("Completed getting from backend job")
    errorResult?.also {
      Timber.d("Emit error result $it")
      restaurantFlow.emit(Result.ErrorResult(it))
    } ?: run {
      val reservations =
        restaurantDetailsResponseMapper.reservationResponseListToReservations(
          reservationsResponse
        )
      val tables =
        restaurantDetailsResponseMapper.tableResponseListToTables(tablesResponse)
      assignReservationsToTables(reservations, tables)
      dao.updateDetails(id, tables)
      Timber.d("Emitting backend results")
      restaurantFlow.emit(
        Result.BackendResult(dao.data(id))
      )
    }
  }

  private suspend fun getFromDatabaseAndEmit(
    id: String,
  ) {
    val restaurantEntity = dao.data(id)
    Timber.d("Emitting db result $restaurantEntity")
    restaurantFlow.emit(Result.DatabaseResult(restaurantEntity))
  }

  private fun assignReservationsToTables(reservations: List<Reservation>, tables: List<Table>) {
    tables.forEach { table ->
      val reservationList = arrayListOf<Reservation>()
      reservations.forEach { reservation ->
        if (table.number == reservation.tableNumber) {
          reservationList.add(reservation)
        }
      }
      table.reservations = reservationList
    }
  }

}