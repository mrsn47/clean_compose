package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.data.db.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

interface GetRestaurantDetailsUseCase {

    suspend operator fun invoke(id: String) : Flow<RestaurantEntity>

}