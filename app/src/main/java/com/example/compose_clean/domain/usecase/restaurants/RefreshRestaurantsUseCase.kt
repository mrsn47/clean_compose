package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.common.GenericResult
import javax.inject.Inject

interface RefreshRestaurantsUseCase {

    suspend operator fun invoke(city: String, search: String): GenericResult<Unit>

}

class RefreshRestaurantsUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : RefreshRestaurantsUseCase {
    override suspend operator fun invoke(city: String, search: String) = restaurantsRepository.refreshRestaurants(city, search)
}