package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.common.FlowResult
import javax.inject.Inject

interface GetRestaurantsUseCase {

    suspend operator fun invoke() : FlowResult<List<RestaurantEntity>>

}

class GetRestaurantsUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetRestaurantsUseCase {
    override suspend operator fun invoke() = restaurantsRepository.restaurants()
}