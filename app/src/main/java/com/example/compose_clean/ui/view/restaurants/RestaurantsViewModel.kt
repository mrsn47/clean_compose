package com.example.compose_clean.ui.view.restaurants

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.RestaurantsFlowUseCase
import com.example.compose_clean.ui.view.states.ProgressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val restaurantsFlowUseCase: RestaurantsFlowUseCase,
    private val refreshRestaurantsUseCase: RefreshRestaurantsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProgressState>(ProgressState.LoadingProgressState)

    val state: StateFlow<ProgressState> = _state
    val restaurants = MutableLiveData<List<RestaurantEntity>>()
    val error = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                restaurantsFlowUseCase().collect {
                    if (it.isEmpty()) {
                        _state.value = ProgressState.EmptyProgressState
                    } else {
                        _state.value = ProgressState.LoadedProgressState
                    }
                    restaurants.postValue(it)
                }
            }
        }
    }

    fun refresh(city: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = refreshRestaurantsUseCase(city)
                result.error?.let {
                    error.postValue(it.localizedMessage)
                }
            }
        }
    }

}


