package com.example.compose_clean.data.mapper

import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.db.model.RestaurantEntity

interface RestaurantMapper {

    fun RestaurantResponse.toEntity(): RestaurantEntity {
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

    fun List<RestaurantResponse>.toEntity(): List<RestaurantEntity> {
        return this.map {
            it.toEntity()
        }
    }
}