package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.common.FlowResult

interface GetRestaurantDetailsUseCase {

    suspend operator fun invoke(id: String) : FlowResult<RestaurantEntity>

}

class GetRestaurantDetailsUseCaseImpl(
    private val restaurantsRepository: RestaurantRepository
) : GetRestaurantDetailsUseCase {
    override suspend operator fun invoke(id: String) = restaurantsRepository.restaurantDetails(id)
}