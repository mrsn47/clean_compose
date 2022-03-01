package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.view.states.GenericResult
import kotlinx.coroutines.flow.Flow

interface GetCitiesUseCase {

    suspend operator fun invoke() : GenericResult<List<String>>

}