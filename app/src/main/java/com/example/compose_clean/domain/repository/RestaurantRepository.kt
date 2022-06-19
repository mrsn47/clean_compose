package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.containsExt
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.data.DataStoreManager
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantResponseMapper
import com.example.compose_clean.common.FlowResult
import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.common.Result.BackendResult
import com.example.compose_clean.common.Result.DatabaseResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
@ExperimentalCoroutinesApi
class RestaurantRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val restaurantResponseMapper: RestaurantResponseMapper,
    private val dao: RestaurantDao,
    private val restaurantApi: RestaurantApi,
    private val cityApi: CityApi
) {

    private lateinit var latestSelectedCity: String
    private var latestSearch = ""
    private var resultIsFromBackend = false

    // todo: receive the db data once per screen initialization,
    //  make dao return the list instead of flow, create seperate flow and post there the db and backend data
    //  like RestaurantDetailsRepository
    suspend fun restaurants(): FlowResult<List<RestaurantEntity>> = channelFlow {
        val selectedCity = getSelectedCity().first()
        latestSelectedCity = selectedCity
        dao.dataFlow().collectLatest {
            Timber.d("Got restaurants from data flow")
            // todo: search functionality should be extracted and refined
            it.filter {
                (it.name.containsExt(latestSearch) || it.type.containsExt(latestSearch)) && (it.city == latestSelectedCity)
            }.also {
                if (resultIsFromBackend) {
                    send(BackendResult(it))
                } else {
                    send(DatabaseResult(it))
                }
            }
        }
    }

    suspend fun refreshRestaurants(city: String, search: String = ""): GenericResult<Unit> =
        safeResultWithContext(Dispatchers.IO) {
            Timber.d("Refreshing restaurants")
            latestSelectedCity = city
            latestSearch = search
            saveSelectedCity(city)
            Timber.d("Saved city $city")

            val restaurantsResponse = getRestaurants(city, search)
            val mappedResult = restaurantResponseMapper.restaurantResponseListToEntities(restaurantsResponse)

            resultIsFromBackend = true
            val restaurantsWithoutImage = dao.addOrUpdateDetails(mappedResult)
            updateImagesFor(restaurantsWithoutImage)
            resultIsFromBackend = false
        }

    private suspend fun getRestaurants(
        city: String,
        search: String
    ): List<RestaurantResponse> {
        Timber.d("Getting restaurants from backend for $city")
        val result = restaurantApi.getRestaurants(city, search)
        Timber.d("Got restaurants from backend for $city, updating database")
        return result
    }

    private suspend fun updateImagesFor(restaurantEntities: List<RestaurantEntity>) {
        Timber.d("Fetching restaurant images")
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
            Timber.d("Launched coroutines for fetching all restaurant images")
            jobs.joinAll()
            Timber.d("Fetched all restaurant images, updating database")
            dao.update(restaurantEntities)
        }
    }

    fun getSelectedCity(): Flow<String> = dataStoreManager.getSelectedCity()

    suspend fun getCities(): GenericResult<List<String>> = safeResultWithContext(Dispatchers.IO) {
        cityApi.getCities()
    }

    suspend fun saveSelectedCity(city: String) = dataStoreManager.saveSelectedCity(city)

}