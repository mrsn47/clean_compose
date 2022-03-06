package com.example.compose_clean.ui.view.restaurants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.common.debug
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val refreshRestaurantsUseCase: RefreshRestaurantsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiProgress>(UiProgress.LoadingProgressState)
    val state: StateFlow<UiProgress> = _state

    private val _data = MutableStateFlow(UiData(mapOf(), null, null))
    val data: StateFlow<UiData> = _data

    init {
        Timber.d("Activity Created")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantsUseCase().collect {
                    Timber.d("getRestaurantsUseCase collected")
                    if (it.isEmpty()) {
                        _state.value = UiProgress.EmptyProgressState
                    } else {
                        _state.value = UiProgress.LoadedProgressState
                    }
                    _data.update { state ->
                        val groupedRestaurants = it.groupBy { it.type }
                        state.copy(groupedRestaurants = groupedRestaurants)
                    }
                    _state.update {
                        UiProgress.LoadedProgressState
                    }
                }
            }
        }
    }

    suspend fun launchedEffect(search: String) {
        Timber.d("launchedEffect")
        val city = getSelectedCityUseCase().first()
        Timber.d("Got city $city in launchedEffect")
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
                Timber.d("Filtering restaurants")
                val result = refreshRestaurantsUseCase(city, search)
                _data.update { state ->
                    state.copy(
                        genericError = result.error?.localizedMessage?.let {
                            GenericError(error = it)
                        }
                    )
                }
                Timber.d("Finished filtering restaurants")
            }
        }
    }

    sealed class UiProgress {
        object LoadingProgressState : UiProgress()
        object LoadedProgressState : UiProgress()
        object EmptyProgressState : UiProgress()
    }

    data class UiData(
        val groupedRestaurants: Map<String, List<RestaurantEntity>>? = null,
        val selectedCity: String? = null,
        val genericError: GenericError? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
        data class NavigateToChangeCity(val navController: NavController) : Event()
    }

}


