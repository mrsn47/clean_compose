package com.example.compose_clean.ui.view.restaurants.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurants.GetCitiesUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetSelectedCityUseCase
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.GetRestaurantsUseCase
import com.example.compose_clean.ui.view.states.ProgressState
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
    private val getCitiesUseCase: GetCitiesUseCase,
    private val refreshRestaurantsUseCase: RefreshRestaurantsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProgressState>(ProgressState.LoadingProgressState)

    val state: StateFlow<ProgressState> = _state
    val restaurants = MutableLiveData<List<RestaurantEntity>>()
    val selectedCity = MutableLiveData<String>()
    val cityList = MutableLiveData<List<String>>()
    val error = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val city = getSelectedCityUseCase()
                selectedCity.postValue(city)
                getRestaurantsUseCase(city).collect {
                    if (it.isEmpty()) {
                        _state.value = ProgressState.EmptyProgressState
                    } else {
                        _state.value = ProgressState.LoadedProgressState
                    }
                    restaurants.postValue(it)
                }
            }
        }
        viewModelScope.launch {
            val result = getCitiesUseCase()
            if(result.hasData) {
                cityList.postValue(result.data!!)
            } else {
                result.error?.let {
                }
            }
        }
    }

    fun sendEvent(event: Event) {
        event.run {
            when (this) {
                is Event.FilterRestaurants -> {
                    filterRestaurants(city, search)
                }
            }
        }
    }

    private fun filterRestaurants(city: String, search: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = refreshRestaurantsUseCase(city, search)
                result.error?.let {
                    error.postValue(it.localizedMessage)
                }
            }
        }
    }

    sealed class Event {
        data class FilterRestaurants(val city: String, val search: String) : Event()
    }

}


