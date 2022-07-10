package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.common.GenericErrorMessage
import com.example.compose_clean.common.Result
import com.example.compose_clean.common.isBetweenIncluding
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.domain.usecase.restaurantdetails.RefreshSingleRestaurantUseCase
import com.example.compose_clean.domain.usecase.restaurantdetails.ReserveTableUseCase
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
  private val reserveTableUseCase: ReserveTableUseCase,
  private val refreshUseCase: RefreshSingleRestaurantUseCase
) : ViewModel() {

  private var restaurantId: String

  private var selectedTableNumber: String? = null
  // todo: just make it zdt not whole slot
  private var selectedStartSlot: TimeSlot? = null
  private var selectedEndSlot: TimeSlot? = null

  private val _info = MutableStateFlow<InfoState>(InfoState.Loading)
  val info: StateFlow<InfoState> = _info

  private val _details = MutableStateFlow<DetailsState>(DetailsState.Loading)
  val details: StateFlow<DetailsState> = _details

  private val _reservationDialog = MutableStateFlow<ReservationDialogState>(ReservationDialogState.NotShown)
  val reservationDialog: StateFlow<ReservationDialogState> = _reservationDialog

  private val _error = MutableStateFlow<ErrorState>(ErrorState.Empty)
  val error: StateFlow<ErrorState> = _error

  init {
    Timber.d("initalized")
    restaurantId = savedStateHandle.get<String>("id")!!
    viewModelScope.launch {
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
    viewModelScope.launch(Dispatchers.Default) {
      event.run {
        when (this) {
          is Event.ClickSlot -> handleSlotClick(tableNumber, timeSlot)
          is Event.ErrorShown -> clearErrorState(errorMessage)
          Event.ReserveClicked -> handleReservation()
          Event.ReservationDoneShown -> {
            _reservationDialog.value = ReservationDialogState.NotShown
          }
        }
      }
    }
  }

  sealed class InfoState {
    object Loading : InfoState()
    data class Loaded(val info: Info) : InfoState()
  }

  sealed class DetailsState {
    object Loading : DetailsState()
    object Empty : DetailsState()
    data class Loaded(val details: Details) : DetailsState()
  }

  sealed class ReservationDialogState {
    object NotShown : ReservationDialogState()
    object Shown : ReservationDialogState()
    object InProgress : ReservationDialogState()
    object Done : ReservationDialogState()
  }

  sealed class ErrorState {
    object Empty : ErrorState()
    class ErrorOccurred(val errorMessage: GenericErrorMessage) : ErrorState()
  }

  sealed class Event {
    data class ClickSlot(val timeSlot: TimeSlot, val tableNumber: String) : Event()
    data class ErrorShown(val errorMessage: GenericErrorMessage) : Event()
    object ReserveClicked: Event()
    object ReservationDoneShown: Event()
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
    data.let {
      if (it.tables.isNullOrEmpty()) {
        if (fromBackend) {
          _details.value = DetailsState.Empty
        } else {
          _details.value = DetailsState.Loading
        }
      } else {
        _details.value = DetailsState.Loaded(
          Details(
            createTablesWithSlots(
              it.tables,
              it.openingTime,
              it.closingTime,
              ZoneId.of(it.zoneId)
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
        is ErrorState.ErrorOccurred -> {
          if (errorMessage == shownErrorMessage) {
            _error.value = ErrorState.Empty
          }
        }
        else -> {
        }
      }
    }
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
    var incrementingZdt: ZonedDateTime = ZonedDateTime.of(
      LocalDate.now(zoneId), LocalTime.parse(openingTime, formatter), zoneId
    )
    val closingZdt: ZonedDateTime = ZonedDateTime.of(
      LocalDate.now(zoneId), LocalTime.parse(closingTime, formatter), zoneId
    )

    while (incrementingZdt.isBefore(closingZdt)) {
      val slotReservation = reservations.firstOrNull {
        Instant.ofEpochSecond(it.startTime).equals(incrementingZdt.toInstant())
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
          incrementingZdt,
          reservationOwner,
          false
        )
      )
      incrementingZdt = incrementingZdt.plusMinutes(30)
    }

    return returnList
  }

  private fun handleSlotClick(tableNumber: String, timeSlot: TimeSlot) {
    if(_reservationDialog.value == ReservationDialogState.InProgress || _reservationDialog.value == ReservationDialogState.Done) {
      // ignore click
      return
    }

    updateSelectedVariables(tableNumber, timeSlot)
    updateDetailsState()

    if(selectedTableNumber != null && selectedStartSlot != null && selectedEndSlot != null){
      _reservationDialog.value = ReservationDialogState.Shown
    } else {
      _reservationDialog.value = ReservationDialogState.NotShown
    }

  }

  private suspend fun handleReservation() {
    _reservationDialog.value = ReservationDialogState.InProgress
    val result = reserveTableUseCase(restaurantId, selectedTableNumber, selectedStartSlot?.zonedDateTime, selectedEndSlot?.zonedDateTime?.plusMinutes(30))
    if(result.isFailure){
      updateErrorState(result.error)
      _reservationDialog.value = ReservationDialogState.Shown
    } else {
      _reservationDialog.value = ReservationDialogState.Done
      refreshUseCase(restaurantId)
    }
  }

  private fun updateSelectedVariables(tableNumber: String, timeSlot: TimeSlot) {
    if (selectedTableNumber != tableNumber) { // if another table is selected clear everything from previous one
      selectedTableNumber = tableNumber
      selectedStartSlot = timeSlot
      selectedEndSlot = null
      return
    }
    if (selectedStartSlot?.zonedDateTime == timeSlot.zonedDateTime) { // if the start slot is reselected
      if(selectedEndSlot != null) {
        selectedStartSlot = timeSlot
        selectedEndSlot = null
      } else {
        selectedStartSlot = null
      }
      return
    }
    if (selectedStartSlot == null) { // if there is not start slot set it
      selectedStartSlot = timeSlot
      return
    }

    if (selectedEndSlot != null) { // if there is start slot and end slot already, set just start slot
      selectedStartSlot = timeSlot
      selectedEndSlot = null
      return
    }

    // if there is start slot, but not end slot
    if (selectedStartSlot!!.zonedDateTime.isBefore(timeSlot.zonedDateTime)) { // if the selected is after the start slot
      selectedEndSlot = timeSlot
    } else { // if it is not, just set the start slot
      selectedStartSlot = timeSlot
      selectedEndSlot = null
    }
  }

  private fun updateDetailsState() {
    // todo: maybe introduce lenses/optics, arrow.kt library for deep copy
    _details.update { state ->
      if (state is DetailsState.Loaded) {

        state.copy(
          details = state.details.copy(
            tablesWithSlots = state.details.tablesWithSlots.map {
              if (it.table.number == selectedTableNumber) {
                it.copy(
                  slots = it.slots.map { slot ->
                    if (selectedEndSlot != null) {
                      var unavailableSlotBetween = false
                      run slotCheck@{
                        it.slots.forEach {
                          if (it.reservationOwner != ReservationOwner.NOT_RESERVED
                            && it.zonedDateTime.isBetweenIncluding(selectedStartSlot?.zonedDateTime, selectedEndSlot?.zonedDateTime)
                          ) {
                            unavailableSlotBetween = true
                            return@slotCheck
                          }
                        }
                      }
                      if (unavailableSlotBetween) {
                        updateErrorState("There's an unavailable slot/s between")
                        selectedEndSlot = null
                        slot.copy()
                      } else {
                        slot.copy(
                          selected = slot.zonedDateTime.isBetweenIncluding(selectedStartSlot?.zonedDateTime, selectedEndSlot?.zonedDateTime)
                        )
                      }
                    } else if (selectedStartSlot != null) {
                      slot.copy(
                        selected = slot.zonedDateTime.isEqual(selectedStartSlot!!.zonedDateTime)
                      )
                    } else {
                      slot.copy(
                        selected = false
                      )
                    }
                  }
                )
              } else {
                it.copy(
                  slots = it.slots.map {
                    it.copy(
                      selected = false
                    )
                  }
                )
              }
            }
          )
        )
      } else {
        state
      }
    }
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