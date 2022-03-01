package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class RefreshRestaurantsUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : RefreshRestaurantsUseCase {
    override suspend operator fun invoke(city: String, search: String) = restaurantsRepository.refresh(city, search)
}