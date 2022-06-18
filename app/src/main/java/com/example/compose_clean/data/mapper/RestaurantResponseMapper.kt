package com.example.compose_clean.data.mapper

import com.example.compose_clean.common.Validator.notNullOrEmpty
import com.example.compose_clean.common.safeCall
import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.db.model.entity.RestaurantEntity

private const val DEFAULT_TIMEZONE = "Europe/Skopje"

class RestaurantResponseMapper {

    fun restaurantResponseToEntity(data: RestaurantResponse): RestaurantEntity {
        data.let {
            return RestaurantEntity(
                id = notNullOrEmpty(it.id, "Id of RestaurantResponse is null is null or empty"),
                address =  notNullOrEmpty(it.address, "Address of RestaurantResponse is null or empty"),
                city = notNullOrEmpty(it.city, "City of RestaurantResponse is null or empty"),
                menuUrl = it.menuUrl,
                name = notNullOrEmpty(it.name, "Name of RestaurantResponse is null or empty"),
                price = it.price,
                type = notNullOrEmpty(it.type, "Type of RestaurantResponse is null or empty"),
                openingTime = notNullOrEmpty(it.openingTime, "Opening time is null or empty"),
                closingTime = notNullOrEmpty(it.closingTime, "Closing time is null or empty"),
                zoneId = notNullOrEmpty(it.zoneId, "Zone id is null or empty"),
                tables = listOf(),
                mainImageDownloadUrl = null
            )
        }
    }

    fun restaurantResponseListToEntities(data: List<RestaurantResponse>): List<RestaurantEntity> {
        return data.mapNotNull {
            safeCall { restaurantResponseToEntity(it) }
        }
    }


}