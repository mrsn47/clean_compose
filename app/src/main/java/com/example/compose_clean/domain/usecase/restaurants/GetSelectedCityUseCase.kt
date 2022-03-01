package com.example.compose_clean.domain.usecase.restaurants

interface GetSelectedCityUseCase {

    suspend operator fun invoke(): String

}