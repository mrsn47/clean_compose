package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.DetailedRestaurant
import com.example.compose_clean.ui.view.restaurants.restaurantdetails.model.Details
import com.example.compose_clean.common.GenericError
import com.example.compose_clean.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
) : ViewModel() {

    private val _progress = MutableStateFlow<ProgressState>(ProgressState.Loading)
    val progress: StateFlow<ProgressState> = _progress

    private val _data = MutableStateFlow(DataState(null, null))
    val data: StateFlow<DataState> = _data

    suspend fun launchedEffect(id: String) {
        Timber.d("launchedEffect")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantDetailsUseCase(id).collect { result ->
                    Timber.d("Collected data in restaurant details viewmodel $result ${result.data}")
                    val detailedRestaurant = mapEntityToUiModel(result.data)
                    when (result) {
                        is Result.BackendResult -> {
                            if (detailedRestaurant.details.tables.isEmpty()) {
                                _progress.value = ProgressState.Empty
                            } else {
                                _progress.value = ProgressState.Loaded
                            }
                        }
                        is Result.DatabaseResult -> { }
                    }
                    _data.update { state ->
                        state.copy(detailedRestaurant = detailedRestaurant)
                    }
                }
            }
        }
    }

    fun sendEvent(event: Event) {
        event.run {
            when (this) {

            }
        }
    }

    sealed class ProgressState {
        object Loading : ProgressState()
        object Loaded : ProgressState()
        object Empty : ProgressState()
    }

    data class DataState(
        val detailedRestaurant: DetailedRestaurant? = null,
        val genericError: GenericError? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
    }

    private fun mapEntityToUiModel(entity: RestaurantEntity): DetailedRestaurant {
        return DetailedRestaurant(
            address = entity.address,
            menuUrl = entity.menuUrl,
            name = entity.name,
            price = entity.price,
            type = entity.type,
            // todo: adjust opening time with the zone id
            openingTime = entity.openingTime,
            closingTime = entity.closingTime,
            details = Details(
                reservations = entity.reservations,
                tables = entity.tables
            ),
            mainImageDownloadUrl = entity.mainImageDownloadUrl,
        )
    }
}