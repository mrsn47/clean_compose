package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.common.Result
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import kotlinx.coroutines.flow.SharedFlow
import org.threeten.bp.ZonedDateTime

interface RestaurantDetailsRepository {

    /**
     * Subscribe to flow
     */
    suspend fun restaurantDetails(id: String): SharedFlow<Result<RestaurantEntity>>

    suspend fun refresh(id: String)

    suspend fun reserveTable(restaurantId: String, tableNumber: String, startTime: ZonedDateTime, endTime: ZonedDateTime): GenericResult<Unit>

}