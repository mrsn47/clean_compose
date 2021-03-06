package com.example.compose_clean.ui.view.restaurants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurants.GetRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetSelectedCityUseCase
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.nav.Screen
import com.example.compose_clean.common.GenericErrorMessage
import com.example.compose_clean.common.Result
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

    private val _progress = MutableStateFlow<UiProgress>(UiProgress.LoadingProgressState)
    val progress: StateFlow<UiProgress> = _progress

    private val _data = MutableStateFlow(UiData(mapOf(), null, null))
    val data: StateFlow<UiData> = _data

    init {
        Timber.d("RestaurantsViewModel Init")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getRestaurantsUseCase().collect {
                    Timber.d("Collected data in restaurants viewmodel $it")
                    when(it){
                        is Result.BackendResult -> {
                            if (it.data.isEmpty()) {
                                _progress.value = UiProgress.EmptyProgressState
                            } else {
                                _progress.value = UiProgress.LoadedProgressState
                            }
                            _data.update { state ->
                                val groupedRestaurants = it.data.groupBy { it.type }
                                state.copy(groupedRestaurants = groupedRestaurants)
                            }
                        }
                        is Result.DatabaseResult -> {
                            if (it.data.isEmpty()) {
                                _progress.value = UiProgress.LoadingProgressState
                            } else {
                                _progress.value = UiProgress.LoadedProgressState
                            }
                            _data.update { state ->
                                val groupedRestaurants = it.data.groupBy { it.type }
                                state.copy(groupedRestaurants = groupedRestaurants)
                            }
                        }
                        else -> {
                            // todo: make this vm like RestaurantDetailsViewModel,
                            //  errors should be emitted through the flows unlike now where result is returned from the function, ex refreshRestaurantsUseCase
                        }
                    }
                }
            }
        }
    }

    suspend fun launchedEffect(search: String) {
        withContext(Dispatchers.IO) {
            Timber.d("launchedEffect")
            val city = getSelectedCityUseCase().first()
            Timber.d("Got city $city in launchedEffect")
            _data.update { state ->
                state.copy(selectedCity = city)
            }
            filterRestaurants(city, search)
        }
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
                    clearDataState()
                    navController.navigate(Screen.Cities.route)
                }
                is Event.NavigateToDetails -> {
                    navController.navigate(Screen.RestaurantDetails.route + "?id=$id")
                }
                is Event.ErrorShown -> {
                    clearErrorState(errorMessage)
                }
            }
        }
    }

    private fun filterRestaurants(city: String, search: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Timber.d("Filtering restaurants")
                val result = refreshRestaurantsUseCase(city, search)
                result.error?.let {
                    _data.update { state ->
                        state.copy(
                            genericErrorMessage = GenericErrorMessage(error = it)
                        )
                    }
                }
                Timber.d("Finished filtering restaurants")
            }
        }
    }

    private fun clearDataState() {
        _data.value = UiData(mapOf(), null, null)
    }

    private fun clearErrorState(errorMessage: GenericErrorMessage) {
        _data.update { state ->
            state.copy(
                genericErrorMessage = if(state.genericErrorMessage == errorMessage) null else state.genericErrorMessage
            )
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
        val genericErrorMessage: GenericErrorMessage? = null
    )

    sealed class Event {
        data class FilterRestaurants(val city: String?, val search: String) : Event()
        data class NavigateToChangeCity(val navController: NavController) : Event()
        data class NavigateToDetails(val navController: NavController, val id: String) : Event()
        data class ErrorShown(val errorMessage: GenericErrorMessage) : Event()
    }

}


