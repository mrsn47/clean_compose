package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.ui.view.states.GenericResult

interface RefreshRestaurantsUseCase {

    suspend operator fun invoke(city: String, search: String): GenericResult<Unit>

}