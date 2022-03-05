package com.example.compose_clean.ui.view.restaurants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurants.GetRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetSelectedCityUseCase
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.ui.view.states.GenericError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val refreshRestaurantsUseCase: RefreshRestaurantsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiProgress>(UiProgress.LoadingProgressState)
    val state: StateFlow<UiProgress> = _state

    private val _data = MutableStateFlow(UiData(listOf(), null, null))
    val data: StateFlow<UiData> = _data

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantsUseCase().collect {
                    if (it.isEmpty()) {
                        _state.value = UiProgress.EmptyProgressState
                    } else {
                        _state.value = UiProgress.LoadedProgressState
                    }
                    _data.update { state ->
                        state.copy(restaurants = it)
                    }
                    _state.update {
                        UiProgress.LoadedProgressState
                    }
                }
            }
        }
    }

    suspend fun launchedEffect(search: String) {
        val city = getSelectedCityUseCase().first()
        _data.update { state ->
            state.copy(selectedCity = city)
        }
        filterRestaurants(city, search)
    }

    fun sendEvent(event: Event) {
        event.run {
            when (this) {
                is Event.FilterRestaurants -> {
                    if (city != null) {
                        filterRestaurants(city, search)
                    }
                }
                is Event.NavigateToChangeCity -> {
                    navController.navigate(Screen.Cities.route)
                }
            }
        }
    }

    private fun filterRestaurants(city: String, search: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = refreshRestaurantsUseCase(city, search)
                _data.update { state ->
                    state.copy(
                        genericError = result.error?.localizedMessage?.let {
                            GenericError(error = it)
                        }
                    )
                }
            }
        }
    }

    sealed class UiProgress {
        object LoadingProgressState : UiProgress()
        object LoadedProgressState : UiProgress()
        object EmptyProgressState : UiProgress()
    }

    data class UiData(
        val restaurants: List<RestaurantEntity>? = null,
        val selectedCity: String? = null,
        val genericError: GenericError? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
        data class NavigateToChangeCity(val navController: NavController) : Event()
    }

}


