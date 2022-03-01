package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.data.db.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

interface GetRestaurantsUseCase {

    suspend operator fun invoke(city: String) : Flow<List<RestaurantEntity>>

}