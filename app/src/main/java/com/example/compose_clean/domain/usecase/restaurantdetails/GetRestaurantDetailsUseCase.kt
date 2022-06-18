package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.common.FlowResult
import com.example.compose_clean.common.Result
import com.example.compose_clean.domain.repository.RestaurantDetailsRepository
import kotlinx.coroutines.flow.SharedFlow

interface GetRestaurantDetailsUseCase {

    suspend operator fun invoke(id: String) : SharedFlow<Result<RestaurantEntity>>

}

class GetRestaurantDetailsUseCaseImpl(
    private val restaurantDetailsRepository: RestaurantDetailsRepository
) : GetRestaurantDetailsUseCase {
    override suspend operator fun invoke(id: String) = restaurantDetailsRepository.restaurantDetails(id)
}