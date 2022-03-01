package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetSelectedCityUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetSelectedCityUseCase {
    override suspend operator fun invoke(): String = restaurantsRepository.getSelectedCity()
}