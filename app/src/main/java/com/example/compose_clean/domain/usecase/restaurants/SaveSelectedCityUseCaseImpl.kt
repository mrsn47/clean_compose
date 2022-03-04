package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class SaveSelectedCityUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : SaveSelectedCityUseCase {
    override suspend operator fun invoke(city: String) = restaurantsRepository.saveSelectedCity(city)
}