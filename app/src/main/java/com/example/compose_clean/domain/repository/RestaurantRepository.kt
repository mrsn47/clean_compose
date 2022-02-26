package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.common.trySendBlockingExt
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
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
class RestaurantRepository @Inject constructor(
    private val restaurantDao: RestaurantDao,
    private val restaurantApi: RestaurantApi
) : RestaurantMapper {

    suspend fun restaurants(): Flow<List<RestaurantEntity>> = callbackFlow {
        launch {
            restaurantDao.data().collectLatest {
                trySendBlockingExt(it)
            }
        }
        launch {
            refresh(null)
        }
        awaitClose {
            // Do nothing...
        }
    }

    suspend fun refresh(city: String?): GenericResult<Unit> = safeResultWithContext(Dispatchers.IO) {
        val resturantEntities = getRestaurantsDetails(city)
        getRestaurantsImages(resturantEntities)
    }

    private suspend fun getRestaurantsDetails(city: String?): List<RestaurantEntity> {
        val result = if (city != null) {
            restaurantApi.getRestaurantsByCity(city)
        } else {
            restaurantApi.getRestaurants()
        }
        restaurantDao.nuke()
        val mappedResult = result.toEntity()
        restaurantDao.add(mappedResult)
        return mappedResult
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