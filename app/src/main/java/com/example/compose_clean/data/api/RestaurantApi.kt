package com.example.compose_clean.data.api

import androidx.core.net.toUri
import com.example.compose_clean.data.api.dto.RestaurantDto
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RestaurantApi {

    suspend fun getRestaurantsByCity(city: String): List<RestaurantDto> {
        val snapshot = FirebaseDatabase.getInstance().reference.child("Restaurant")
            .orderByChild("city").equalTo(city)
            .get().await()
        val list = arrayListOf<RestaurantDto>()
        snapshot.children.forEach {
            val restaurant = it.getValue(RestaurantDto::class.java)!!
            restaurant.id = it.key!!
            list.add(restaurant)
        }
        return list
    }

    suspend fun getRestaurants() : List<RestaurantDto> {
        val snapshot = FirebaseDatabase.getInstance().reference.child("Restaurant")
            .get().await()
        val list = arrayListOf<RestaurantDto>()
        snapshot.children.forEach {
            val restaurant = it.getValue(RestaurantDto::class.java)!!
            restaurant.id = it.key!!
            list.add(restaurant)
        }
        return list
    }

    suspend fun getRestaurantImageUrl(restaurantId: String): String? {
        return Firebase.storage.reference.child("${restaurantId}.png").downloadUrl.await().toString()
    }
}