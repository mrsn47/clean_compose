package com.example.compose_clean.data.db.dao

import androidx.room.*
import com.example.compose_clean.data.db.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurant")
    fun dataFlow(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurant WHERE id IN (:ids)")
    fun data(ids: List<String>): List<RestaurantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(data: List<RestaurantEntity>)

    @Query("DELETE FROM restaurant")
    fun nuke()

    @Update
    fun update(data: List<RestaurantEntity>)

    /**
     * Adds the [data] to database. Existing entities keep the tables,
     * reservations and download url of main image. Returns restaurants that were not existent and were added.
     *
     * @param data [List] of [RestaurantEntity]
     * @return [List] of [RestaurantEntity]
     * */
    fun addOrUpdateDetails(data: List<RestaurantEntity>): List<RestaurantEntity> {
        val dataMap = data.associateBy({ it.id }, { it })
        val ids = dataMap.keys.toList()
        val existingRestaurants = data(ids)
        existingRestaurants.forEach { entity ->
            dataMap[entity.id]?.apply {
                tables = entity.tables
                reservations = entity.reservations
                mainImageDownloadUrl = entity.mainImageDownloadUrl
            }
        }
        add(dataMap.values.toList())
        val existingRestaurantsIds = existingRestaurants.map { it.id }
        return data.filter {
            it.id !in existingRestaurantsIds
        }
    }

}