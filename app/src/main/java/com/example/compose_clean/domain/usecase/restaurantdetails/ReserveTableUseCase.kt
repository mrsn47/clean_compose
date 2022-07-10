package com.example.compose_clean.domain.usecase.restaurantdetails

import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.common.Validator.notNullOrEmpty
import com.example.compose_clean.common.safeResult
import com.example.compose_clean.domain.repository.RestaurantDetailsRepository
import org.threeten.bp.ZonedDateTime

interface ReserveTableUseCase {

  suspend operator fun invoke(
    restaurantId: String,
    tableNumber: String?,
    startTime: ZonedDateTime?,
    endTime: ZonedDateTime?
  ): GenericResult<Unit>

}

class ReserveTableUseCaseImpl(
  private val restaurantDetailsRepository: RestaurantDetailsRepository
) : ReserveTableUseCase {
  override suspend fun invoke(
    restaurantId: String,
    tableNumber: String?,
    startTime: ZonedDateTime?,
    endTime: ZonedDateTime?
  ): GenericResult<Unit> {
    return if(tableNumber == null || startTime == null || endTime == null) {
      GenericResult(error = "An error occured while reserving")
    } else {
      restaurantDetailsRepository.reserveTable(
        restaurantId, tableNumber, startTime, endTime
      )
    }
  }
}