package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.domain.repository.RestaurantRepository

class GetRestaurantDetailsUseCaseImpl(
    private val restaurantsRepository: RestaurantRepository
) : GetRestaurantDetailsUseCase {
    override suspend operator fun invoke(id: String) = restaurantsRepository.restaurantDetails(id)
}