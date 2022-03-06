package com.example.compose_clean.data.api

import com.example.compose_clean.common.capitalizeExt
import com.example.compose_clean.common.safeCall
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class RestaurantApi {

    /**
     * Gets from firebase by city, performs local filtering
     * */
    suspend fun getRestaurants(city: String, name: String): List<RestaurantResponse> {
        val snapshot = FirebaseDatabase.getInstance().reference.child("Restaurant")
            .orderByChild("city").equalTo(city.capitalizeExt())
            .get().await()
        val list = arrayListOf<RestaurantResponse>()
        snapshot.children.forEach {
            val restaurant = it.getValue(RestaurantResponse::class.java)!!
            restaurant.id = it.key!!
            list.add(restaurant)
        }
        list.filter {
            it.name?.contains(name) == true || it.type?.contains(name) == true
        }
        return list
    }

    suspend fun getRestaurantImageUrl(restaurantId: String): String? = safeCall {
        Firebase.storage.reference.child("${restaurantId}.png").downloadUrl.await().toString()
    }

    suspend fun getRestaurantTables(restaurantId: String): List<TableResponse>? = safeCall {
        val snapshot = FirebaseDatabase.getInstance().reference.child("TableCatalog")
            .child(restaurantId)
            .get().await()
        val list = arrayListOf<TableResponse>()
        snapshot?.children?.forEach {
            val table = it.getValue(TableResponse::class.java)!!
            table.number = it.key!!
            list.add(table)
        }
        return list
    }

    suspend fun getRestaurantReservations(restaurantId: String): List<ReservationResponse>? = safeCall {
        val snapshot = FirebaseDatabase.getInstance().reference.child("Reservation")
            .child(restaurantId)
            .get().await()
        val list = arrayListOf<ReservationResponse>()
        snapshot.children.forEach {
            val reservation = it.getValue(ReservationResponse::class.java)!!
            reservation.id = it.key!!
            list.add(reservation)
        }
        return list
    }
}