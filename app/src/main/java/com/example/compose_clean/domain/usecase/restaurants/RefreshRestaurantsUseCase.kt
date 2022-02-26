package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class RefreshRestaurantsUseCase @Inject constructor(
    private val restaurantsRepository: RestaurantRepository

) {
    suspend operator fun invoke(city: String? = null) = restaurantsRepository.refresh(city)
}