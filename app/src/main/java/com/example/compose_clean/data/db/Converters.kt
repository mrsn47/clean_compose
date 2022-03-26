package com.example.compose_clean.data.db

import androidx.room.TypeConverter
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.model.Reservation
import com.example.compose_clean.data.db.model.Table
import com.google.gson.Gson
import kotlin.reflect.KClass

class Converters {

    @TypeConverter
    fun tableEntityListFromString(value: String): List<Table>? {
        if(value == "null"){
            return null
        }
        return ObjectSerialization.deserialize(value, SerializedTables::class).data
    }

    @TypeConverter
    fun stringFromTableEntityList(value: List<Table>?): String {
        if(value == null){
            return "null"
        }
        val serializedTableEntities = SerializedTables(value)
        return ObjectSerialization.serialize(serializedTableEntities)
    }

    @TypeConverter
    fun reservationEntityListFromString(value: String): List<Reservation>? {
        if(value == "null"){
            return null
        }
        return ObjectSerialization.deserialize(value, SerializedReservations::class).data
    }

    @TypeConverter
    fun stringFromReservationEntityList(value: List<Reservation>?): String {
        if(value == null){
            return "null"
        }
        val serializedReservationEntities = SerializedReservations(value)
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

data class SerializedTables(
    val data: List<Table>
)

data class SerializedReservations(
    val data: List<Reservation>
)