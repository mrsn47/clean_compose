package com.example.compose_clean.data.api

import com.example.compose_clean.data.api.response.RestaurantResponse
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CityApi {

    suspend fun getCities() : List<String> {
        val snapshot = FirebaseDatabase.getInstance().reference.child("Restaurant")
            .get().await()
        val list = arrayListOf<RestaurantResponse>()
        snapshot.children.forEach {
            val restaurant = it.getValue(RestaurantResponse::class.java)!!
            restaurant.id = it.key!!
            list.add(restaurant)
        }
        val cities = list.mapNotNull { it.city }.distinct()
        return cities
    }

}
