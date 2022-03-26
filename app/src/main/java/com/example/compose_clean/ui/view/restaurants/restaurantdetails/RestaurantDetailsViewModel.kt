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

    private val _progress = MutableStateFlow<UiProgress>(UiProgress.LoadingProgressState)
    val progress: StateFlow<UiProgress> = _progress

    private val _data = MutableStateFlow(UiData(null, null))
    val data: StateFlow<UiData> = _data

    suspend fun launchedEffect(id: String) {
        Timber.d("launchedEffect")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantDetailsUseCase(id).collect { result ->
                    Timber.d("Collected data in restaurant details viewmodel $result ${result.data}")
                    val detailedRestaurant = mapEntityToRestaurant(result.data)
                    when (result) {
                        is Result.BackendResult -> {
                            if (detailedRestaurant.details.tables.isEmpty()) {
                                _progress.value = UiProgress.EmptyProgressState
                            } else {
                                _progress.value = UiProgress.LoadedProgressState
                            }
                        }
                        is Result.DatabaseResult -> {

                        }
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

    sealed class UiProgress {
        object LoadingProgressState : UiProgress()
        object LoadedProgressState : UiProgress()
        object EmptyProgressState : UiProgress()
    }

    data class UiData(
        val detailedRestaurant: DetailedRestaurant? = null,
        val genericError: GenericError? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
    }

    private fun mapEntityToRestaurant(entity: RestaurantEntity): DetailedRestaurant {
        return DetailedRestaurant(
            address = entity.address,
            menuUrl = entity.menuUrl,
            name = entity.name,
            price = entity.price,
            type = entity.type,
            details = Details(
                reservations = entity.reservations,
                tables = entity.tables
            ),
            mainImageDownloadUrl = entity.mainImageDownloadUrl,
        )
    }
}