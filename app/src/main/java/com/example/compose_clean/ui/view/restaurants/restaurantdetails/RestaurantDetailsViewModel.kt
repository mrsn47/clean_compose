package com.example.compose_clean.ui.view.restaurants.restaurantdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.ui.view.states.GenericError
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

    private val _state = MutableStateFlow<UiProgress>(UiProgress.LoadingProgressState)
    val state: StateFlow<UiProgress> = _state

    private val _data = MutableStateFlow(UiData(null, null))
    val data: StateFlow<UiData> = _data

    suspend fun launchedEffect(id: String) {
        Timber.d("launchedEffect")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantDetailsUseCase(id).collect { restaurant ->
                    Timber.d("Collected restaurant details in viewmodel")
                    _data.update {
                        it.copy(restaurantDetails = restaurant)
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
        val restaurantDetails: RestaurantEntity? = null,
        val genericError: GenericError? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
        data class NavigateToChangeCity(val navController: NavController) : Event()
    }
}