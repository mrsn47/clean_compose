package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class RestaurantsFlowUseCase @Inject constructor(
    private val restaurantsRepository: RestaurantRepository

) {
    suspend operator fun invoke() = restaurantsRepository.restaurants()
}