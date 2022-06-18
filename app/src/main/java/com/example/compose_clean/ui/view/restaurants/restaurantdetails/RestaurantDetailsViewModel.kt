package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.common.GenericErrorMessage
import com.example.compose_clean.common.Result
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.DetailedRestaurant
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.ReservationOwner
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TableWithSlots
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
) : ViewModel() {
    // todo: merge these flows into one
    private val _progress = MutableStateFlow<ProgressState>(ProgressState.Loading)
    val progress: StateFlow<ProgressState> = _progress

    private val _data = MutableStateFlow(DataState(null, null))
    val data: StateFlow<DataState> = _data

    suspend fun onInitialComposition(id: String) {
        Timber.d("launchedEffect")
        viewModelScope.launch {
            Timber.d("launch")
            withContext(Dispatchers.IO) {
                getRestaurantDetailsUseCase(id).collect { result ->
                    when (result) {
                        is Result.BackendResult -> {
                            Timber.d("Collected backend data ${result.data}")
                            if (result.data.tables.isEmpty()) {
                                updateProgressState(ProgressState.Empty)
                            } else {
                                updateProgressState(ProgressState.Loaded)
                            }
                            updateDataState(result.data)
                        }
                        is Result.DatabaseResult -> {
                            Timber.d("Collected db data ${result.data}")
                            updateDataState(result.data)
                        }
                        is Result.ErrorResult -> {
                            Timber.d("Collected error result ${result.error}")
                            updateDataState(result.error)
                        }
                    }
                }
            }
        }
    }

    fun sendEvent(event: Event) {
        with(event) {
            when (this) {
                is Event.ClickSlot -> {
                    // todo: handle reservation
                }
            }
        }
    }

    private fun updateProgressState(progress: ProgressState) {
        _progress.value = progress
    }

    private fun updateDataState(data: RestaurantEntity) {
        val detailedRestaurant = mapEntityToUiModel(data)
        _data.update { state ->
            state.copy(detailedRestaurant = detailedRestaurant)
        }
    }

    private fun updateDataState(genericError: String) {
        _data.update { state ->
            state.copy(genericError = GenericErrorMessage(genericError))
        }
    }

    sealed class ProgressState {
        object Loading : ProgressState()
        object Loaded : ProgressState()
        object Empty : ProgressState()
    }

    data class DataState(
        val detailedRestaurant: DetailedRestaurant? = null,
        val genericError: GenericErrorMessage? = null
    )

    sealed class Event {
        data class ClickSlot(val selectedTime: ZonedDateTime, val tableNumber: String) : Event()
    }

    private fun mapEntityToUiModel(entity: RestaurantEntity): DetailedRestaurant {
        return DetailedRestaurant(
            address = entity.address,
            menuUrl = entity.menuUrl,
            name = entity.name,
            price = entity.price,
            type = entity.type,
            tablesWithSlots = createTablesWithSlots(
                entity.tables,
                entity.openingTime,
                entity.closingTime,
                ZoneId.of(entity.zoneId)
            ),
            mainImageDownloadUrl = entity.mainImageDownloadUrl,
        )
    }

    private fun createTablesWithSlots(
        tables: List<Table>,
        openingTime: String,
        closingTime: String,
        zoneId: ZoneId
    ): List<TableWithSlots> {
        // openingTime = 08:00, zoneId = "Europe/London" -> openingTime = 09:00
        val returnList: ArrayList<TableWithSlots> = arrayListOf()


        tables.forEach {
            filterTableReservations(it, zoneId)
            returnList.add(
                TableWithSlots(
                    it,
                    createTimeSlots(it.reservations, openingTime, closingTime, zoneId)
                )
            )
        }

        return returnList

    }

    private fun filterTableReservations(table: Table, zoneId: ZoneId) {
        val filteredList = table.reservations.filter {
            val reservationInstant = Instant.ofEpochSecond(it.startTime)
            val nowInstant = Instant.now()

            val reservationLdt =
                LocalDateTime.ofInstant(reservationInstant, ZoneId.systemDefault()).toLocalDate()
            val nowLdt = LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault()).toLocalDate()

            // current day
            reservationLdt.isEqual(nowLdt)
        }
        table.reservations = filteredList
    }

    private fun createTimeSlots(
        reservations: List<Reservation>,
        openingTime: String,
        closingTime: String,
        zoneId: ZoneId
    ): List<TimeSlot> {
        // openingTime = 08:00, zoneId = "Europe/London" -> openingTime = 09:00
        val returnList: ArrayList<TimeSlot> = arrayListOf()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var openingZdt: ZonedDateTime = ZonedDateTime.of(
            LocalDate.now(zoneId), LocalTime.parse(openingTime, formatter), zoneId
        )
        val closingZdt: ZonedDateTime = ZonedDateTime.of(
            LocalDate.now(zoneId), LocalTime.parse(closingTime, formatter), zoneId
        )

        while (openingZdt.isBefore(closingZdt)) {
            val slotReservation = reservations.firstOrNull {
                Instant.ofEpochSecond(it.startTime).equals(openingZdt.toInstant())
            }
            val reservationOwner = if (slotReservation != null) {
//                if(slotReservation.owner == Firebase.auth) {
//                    ReservationOwner.CURRENT_USER
//                } else {
//                    ReservationOwner.OTHER
//                }
                ReservationOwner.OTHER
            } else {
                ReservationOwner.NOT_RESERVED
            }
            returnList.add(
                TimeSlot(
                    openingZdt,
                    reservationOwner
                )
            )
            openingZdt = openingZdt.plusMinutes(30)

        }

        return returnList
    }

}