package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.ui.view.states.GenericResult

interface GetCitiesUseCase {

    suspend operator fun invoke() : GenericResult<List<String>>

}