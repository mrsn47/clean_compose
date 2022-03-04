package com.example.compose_clean.ui.view.restaurants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurants.GetCitiesUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetSelectedCityUseCase
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val refreshRestaurantsUseCase: RefreshRestaurantsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.LoadingProgressState)
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
//                val city = getSelectedCityUseCase().flatMapLatest {
//                    getRestaurantsUseCase(it)
//                }.collect {
//                    if (it.isEmpty()) {
//                        _state.value = ProgressState.EmptyProgressState
//                    } else {
//                        _state.value = ProgressState.LoadedProgressState
//                    }
//                    restaurants.postValue(it)
//                }
                getRestaurantsUseCase().collect {
                    if (it.isEmpty()) {
                        _state.value = UiState.EmptyProgressState
                    } else {
                        _state.value = UiState.LoadedProgressState
                    }
                    _state.value = UiState.Data(restaurantData = it)
                }
            }
        }
    }

    suspend fun launchedEffect(search: String) {
        val city = getSelectedCityUseCase().first()
        _state.value = UiState.Data(selectedCityData = city)
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
                result.error?.localizedMessage?.let {
                    _state.value = UiState.GenericError(it)
                }
            }
        }
    }

    fun test() {
        val da = 5
        val ne = 4
    }

    sealed class UiState {
        object LoadingProgressState : UiState()
        object LoadedProgressState : UiState()
        object EmptyProgressState : UiState()
        class Data(
            val restaurantData: List<RestaurantEntity>? = null,
            val selectedCityData: String? = null,
        ) : UiState()

        class GenericError(val error: String) : UiState()
    }

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
        data class NavigateToChangeCity(val navController: NavController) : Event()
    }

}


