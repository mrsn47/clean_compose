package com.example.compose_clean.domain.usecase.restaurants

import kotlinx.coroutines.flow.Flow

interface GetSelectedCityUseCase {

    suspend operator fun invoke(): Flow<String>

}