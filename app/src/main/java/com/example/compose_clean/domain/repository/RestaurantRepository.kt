package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.containsExt
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.common.trySendBlockingExt
import com.example.compose_clean.data.DataStoreManager
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantMapper
import com.example.compose_clean.ui.view.states.GenericResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
@ExperimentalCoroutinesApi
class RestaurantRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val restaurantDao: RestaurantDao,
    private val restaurantApi: RestaurantApi,
    private val cityApi: CityApi
) : RestaurantMapper {

    lateinit var latestSelectedCity: String
    var latestSearch = ""

    suspend fun restaurants(): Flow<List<RestaurantEntity>> = channelFlow {
        val selectedCity = getSelectedCity().first()
        latestSelectedCity = selectedCity
        restaurantDao.dataFlow().collectLatest {
            Timber.d("Got restaurants from data flow")
            // todo: search functionality should be extracted and refined
            it.filter {
                (it.name.containsExt(latestSearch) || it.type.containsExt(latestSearch)) && (it.city == latestSelectedCity)
            }.also {
                send(it)
            }
        }
    }

    // todo: might need tweaks, see how it's handling when back is pressed while on restaurant details screen
    suspend fun restaurantDetails(id: String): Flow<RestaurantEntity> = callbackFlow {
        Timber.d("Fetching restaurant details for id $id")
        var tables: List<TableResponse>? = null
        var reservations: List<ReservationResponse>? = null
        val job = CoroutineScope(Dispatchers.IO).launch {
            restaurantDao.data(id).collect {
                Timber.d("Collected restaurant details $it")
                trySendBlockingExt(it)
            }
        }
        withContext(Dispatchers.IO) {
            launch {
                Timber.d("Fetching restaurant tables")
                tables = restaurantApi.getRestaurantTables(id)
                Timber.d("Got restaurant tables $tables")
            }
            launch {
                Timber.d("Fetching restaurant reservations")
                reservations = restaurantApi.getRestaurantReservations(id)
                Timber.d("Got restaurant reservations $reservations")
            }
        }
        Timber.d("Finished fetching restaurant details, updating entity")
        restaurantDao.updateDetails(id, tables, reservations)
        awaitClose {
            Timber.d("awaitClose triggered, cancelling details collecting coroutine")
            job.cancel()
        }
    }

    suspend fun refreshRestaurants(city: String, search: String = ""): GenericResult<Unit> =
        safeResultWithContext(Dispatchers.IO) {
            Timber.d("Refreshing restaurants")
            latestSelectedCity = city
            latestSearch = search
            saveSelectedCity(city)
            Timber.d("Saved city $city")
            val addedRestaurants = getRestaurants(city, search)
            getRestaurantsImages(addedRestaurants)
        }

    private suspend fun getRestaurants(
        city: String,
        search: String
    ): List<RestaurantEntity> {
        val result = restaurantApi.getRestaurants(city, search)
        Timber.d("Got restaurants from api $city, updating database")
        val mappedResult = result.toEntity()
        return restaurantDao.addOrUpdateDetails(mappedResult)
    }

    private suspend fun getRestaurantsImages(restaurantEntities: List<RestaurantEntity>) {
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
            restaurantDao.update(restaurantEntities)
        }
    }

    fun getSelectedCity(): Flow<String> = dataStoreManager.getSelectedCity()

    suspend fun getCities(): GenericResult<List<String>> = safeResultWithContext(Dispatchers.IO) {
        cityApi.getCities()
    }

    suspend fun saveSelectedCity(city: String) = dataStoreManager.saveSelectedCity(city)

}