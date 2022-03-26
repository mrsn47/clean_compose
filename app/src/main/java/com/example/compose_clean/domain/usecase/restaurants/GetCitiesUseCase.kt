package com.example.compose_clean.domain.usecase.restaurants

import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.common.GenericResult
import javax.inject.Inject

interface GetCitiesUseCase {

    suspend operator fun invoke() : GenericResult<List<String>>

}

class GetCitiesUseCaseImpl  @Inject constructor(
    private val restaurantsRepository: RestaurantRepository
) : GetCitiesUseCase {
    override suspend operator fun invoke() = restaurantsRepository.getCities()
}