package com.example.compose_clean.data.db.dao

import androidx.room.*
import com.example.compose_clean.data.db.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurant")
    fun data(): Flow<List<RestaurantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(data: List<RestaurantEntity>)

    @Query("DELETE FROM restaurant")
    fun nuke()

    @Update
    fun update(data: List<RestaurantEntity>)
}