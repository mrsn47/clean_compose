package com.example.compose_clean.data.mapper

import com.example.compose_clean.data.api.dto.RestaurantDto
import com.example.compose_clean.data.db.model.RestaurantEntity

interface RestaurantMapper {

    fun RestaurantDto.toEntity(): RestaurantEntity {
        return RestaurantEntity(
            id = id!!,
            address = address!!,
            city = city!!,
            menuUrl = menuUrl!!,
            name = name!!,
            price = price!!,
            type = type!!,
            null,
            null,
            null
        )
    }

    fun List<RestaurantDto>.toEntity(): List<RestaurantEntity> {
        return this.map {
            it.toEntity()
        }
    }
}