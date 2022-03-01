package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetCitiesUseCaseImpl  @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetCitiesUseCase {
    override suspend operator fun invoke() = restaurantsRepository.getCities()
}