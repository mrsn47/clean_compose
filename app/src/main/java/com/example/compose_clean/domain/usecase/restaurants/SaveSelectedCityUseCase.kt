package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.ui.view.states.GenericResult

interface SaveSelectedCityUseCase {

    suspend operator fun invoke(city: String)

}