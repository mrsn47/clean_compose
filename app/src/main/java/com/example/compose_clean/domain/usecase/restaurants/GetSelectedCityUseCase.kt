package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetSelectedCityUseCase {

    suspend operator fun invoke(): Flow<String>

}

class GetSelectedCityUseCaseImpl @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetSelectedCityUseCase {
    override suspend operator fun invoke(): Flow<String> = restaurantsRepository.getSelectedCity()
}