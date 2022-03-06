package com.example.compose_clean.data.db

import androidx.room.TypeConverter
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.google.gson.Gson
import kotlin.reflect.KClass

class Converters {

    @TypeConverter
    fun tableEntityListFromString(value: String): List<TableResponse>? {
        if(value == "null"){
            return null
        }
        return ObjectSerialization.deserialize(value, SerializedTableEntities::class).data
    }

    @TypeConverter
    fun stringFromTableEntityList(value: List<TableResponse>?): String {
        if(value == null){
            return "null"
        }
        val serializedTableEntities = SerializedTableEntities(value)
        return ObjectSerialization.serialize(serializedTableEntities)
    }

    @TypeConverter
    fun reservationEntityListFromString(value: String): List<ReservationResponse>? {
        if(value == "null"){
            return null
        }
        return ObjectSerialization.deserialize(value, SerializedReservationEntities::class).data
    }

    @TypeConverter
    fun stringFromReservationEntityList(value: List<ReservationResponse>?): String {
        if(value == null){
            return "null"
        }
        val serializedReservationEntities = SerializedReservationEntities(value)
        return ObjectSerialization.serialize(serializedReservationEntities)
    }

}

object ObjectSerialization {
    private val gson = Gson()
    fun serialize(data: Any?): String {
        return gson.toJson(data)
    }

    fun <T : Any> deserialize(data: String, clazz: KClass<T>): T {
        return gson.fromJson(data, clazz.java)
    }
}

data class SerializedTableEntities(
    val data: List<TableResponse>
)

data class SerializedReservationEntities(
    val data: List<ReservationResponse>
)