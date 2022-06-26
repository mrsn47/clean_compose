package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.common.GenericErrorMessage
import com.example.compose_clean.common.Result
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsViewModel.ErrorState.ErrorOccurred
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.RestaurantDetailsViewModel.Info
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.DetailedRestaurant
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.ReservationOwner
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TableWithSlots
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
) : ViewModel() {

    private val _info = MutableStateFlow<InfoState>(InfoState.Loading)
    val info: StateFlow<InfoState> = _info

    private val _details = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val details: StateFlow<DetailsState> = _details

    private val _error = MutableStateFlow<ErrorState>(ErrorState.Empty)
    val error: StateFlow<ErrorState> = _error

    init {
        Timber.d("initalized")
        val restaurantId = savedStateHandle.get<String>("id")!!
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("launch")
            getRestaurantDetailsUseCase(restaurantId).collect { result ->
                when (result) {
                    is Result.BackendResult -> {
                        Timber.d("Collected backend data ${result.data}")
                        updateInfoState(result.data)
                        updateDetailsState(result.data, true)
                    }
                    is Result.DatabaseResult -> {
                        Timber.d("Collected db data ${result.data}")
                        updateInfoState(result.data)
                        updateDetailsState(result.data, false)
                    }
                    is Result.ErrorResult -> {
                        Timber.d("Collected error result ${result.error}")
                        updateErrorState(result.error)
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
                is Event.ErrorShown -> {
                    clearErrorState(errorMessage)
                }
            }
        }
    }

    private fun updateInfoState(data: RestaurantEntity) {
        data.run {
            _info.value = InfoState.Loaded(
                Info(
                    address,
                    menuUrl,
                    name,
                    price,
                    type,
                    mainImageDownloadUrl
                )
            )
        }
    }

    private fun updateDetailsState(data: RestaurantEntity, fromBackend: Boolean) {
        data.run {
            if (this.tables.isNullOrEmpty()) {
                if (fromBackend) {
                    _details.value = DetailsState.Empty
                } else {
                    _details.value = DetailsState.Loading
                }
            } else {
                _details.value = DetailsState.Loaded(
                    Details(
                        createTablesWithSlots(
                            tables,
                            openingTime,
                            closingTime,
                            ZoneId.of(zoneId)
                        )
                    )
                )
            }
        }
    }

    private fun updateErrorState(errorMessage: String?) {
        if (errorMessage != null) {
            _error.value = ErrorState.ErrorOccurred(GenericErrorMessage(errorMessage))
        } else {
            _error.value = ErrorState.Empty
        }
    }

    private fun clearErrorState(shownErrorMessage: GenericErrorMessage) {
        _error.value.run {
            when (this) {
                is ErrorOccurred -> {
                    if (errorMessage == shownErrorMessage) {
                        _error.value = ErrorState.Empty
                    }
                }
                else -> {
                }
            }
        }
    }

    sealed class InfoState {
        object Loading : InfoState()
        class Loaded(val info: Info) : InfoState()
    }

    sealed class DetailsState {
        object Loading : DetailsState()
        object Empty : DetailsState()
        class Loaded(val details: Details) : DetailsState()
    }

    sealed class ErrorState {
        object Empty : ErrorState()
        class ErrorOccurred(var errorMessage: GenericErrorMessage) : ErrorState()
    }

    sealed class Event {
        data class ClickSlot(val selectedTime: ZonedDateTime, val tableNumber: String) : Event()
        data class ErrorShown(val errorMessage: GenericErrorMessage) : Event()
    }

    private fun createTablesWithSlots(
        tables: List<Table>,
        openingTime: String,
        closingTime: String,
        zoneId: ZoneId
    ): List<TableWithSlots> {
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

    data class Info(
        val address: String,
        val menuUrl: String?,
        val name: String,
        val price: Int?,
        val type: String,
        val mainImageDownloadUrl: String?
    )

    data class Details(
        val tablesWithSlots: List<TableWithSlots>,
    )

}