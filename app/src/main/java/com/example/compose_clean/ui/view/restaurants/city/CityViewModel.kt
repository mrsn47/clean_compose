package com.example.compose_clean.ui.view.restaurants.city

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.compose_clean.domain.usecase.restaurants.GetCitiesUseCase
import com.example.compose_clean.domain.usecase.restaurants.SaveSelectedCityUseCase
import com.example.compose_clean.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val saveSelectedCityUseCase: SaveSelectedCityUseCase
) : ViewModel() {

    val cities = MutableLiveData<List<String>>()

    init {
        viewModelScope.launch {
            val result = getCitiesUseCase()
            if (result.hasData) {
                cities.postValue(result.data!!)
            } else {
                result.error?.let {
                }
            }
        }
    }

    // todo: save in prefs and navigate
    fun navigateToRestaurantsScreen(navController: NavController, city: String) {
        viewModelScope.launch {
            saveSelectedCityUseCase(city)
            navController.popBackStack(Screen.Restaurants.route, false)
        }
    }

}

