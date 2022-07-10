package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.domain.repository.RestaurantDetailsRepository

interface RefreshSingleRestaurantUseCase {

  suspend operator fun invoke(
    restaurantId: String
  )

}

class RefreshSingleRestaurantUseCaseImpl(
  private val restaurantDetailsRepository: RestaurantDetailsRepository
) : RefreshSingleRestaurantUseCase {
  override suspend fun invoke(restaurantId: String) {
    restaurantDetailsRepository.refresh(restaurantId)
  }

}