package com.example.compose_clean.domain.repository

import android.content.SharedPreferences
import com.example.compose_clean.common.containsExt
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.common.trySendBlockingExt
import androidx.core.content.edit
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantMapper
import com.example.compose_clean.ui.view.states.GenericResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.selects.select
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
class RestaurantRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val restaurantDao: RestaurantDao,
    private val restaurantApi: RestaurantApi,
    private val cityApi: CityApi
) : RestaurantMapper {

    companion object {
        const val DEFAULT_CITY = "Skopje"
        const val SELECTED_CITY = "SELECTED_CITY"
    }

    lateinit var latestSearchedCity: String
    var latestSearch = ""

    suspend fun restaurants(city: String): Flow<List<RestaurantEntity>> = callbackFlow {
        launch {
            restaurantDao.dataFlow().collectLatest {
                // todo: search functionality should be extracted and refined
                val filtered = it.filter {
                    it.name.containsExt(latestSearch) || it.type.containsExt(latestSearch) && it.city == latestSearchedCity
                }
                trySendBlockingExt(filtered)
            }
        }
        launch {
            refresh(city)
        }
        awaitClose {
            // Do nothing...
        }
    }

    suspend fun refresh(city: String, search: String = ""): GenericResult<Unit> = safeResultWithContext(Dispatchers.IO) {
        latestSearchedCity = city
        latestSearch = search
        saveSelectedCity(city)
        val addedRestaurants = getRestaurantsDetails(city, search)
        getRestaurantsImages(addedRestaurants)
    }

    fun getSelectedCity(): String {
        val selectedCity = sharedPreferences.getString(SELECTED_CITY, DEFAULT_CITY)
        return selectedCity!!
    }

    suspend fun getCities(): GenericResult<List<String>> = safeResultWithContext(Dispatchers.IO) {
        cityApi.getCities()
    }

    private fun saveSelectedCity(city: String){
        sharedPreferences.edit(commit = true) {
            putString(SELECTED_CITY, city)
        }
    }

    private suspend fun getRestaurantsDetails(city: String, search: String): List<RestaurantEntity> {
        val result = restaurantApi.getRestaurants(city, search)
        val mappedResult = result.toEntity()
        return restaurantDao.addOrUpdateDetails(mappedResult)
    }

    private suspend fun getRestaurantsImages(restaurantEntities: List<RestaurantEntity>) {
        val jobs = arrayListOf<Job>()
        coroutineScope {
            restaurantEntities.forEach {
                jobs.add(
                    launch {
                        val imageUrl = restaurantApi.getRestaurantImageUrl(it.id)
                        it.mainImageDownloadUrl = imageUrl
                    }
                )
            }
            jobs.joinAll()
            restaurantDao.update(restaurantEntities)
        }
    }
}