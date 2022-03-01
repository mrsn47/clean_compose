package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRestaurantsUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetRestaurantsUseCase {
    override suspend operator fun invoke(city: String) = restaurantsRepository.restaurants(city)
}