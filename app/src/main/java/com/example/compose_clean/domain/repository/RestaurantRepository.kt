package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.containsExt
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.common.trySendBlockingExt
import com.example.compose_clean.data.DataStoreManager
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.api.response.ReservationResponse
import com.example.compose_clean.data.api.response.RestaurantResponse
import com.example.compose_clean.data.api.response.TableResponse
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.entity.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantResponseMapper
import com.example.compose_clean.common.FlowResult
import com.example.compose_clean.common.GenericResult
import com.example.compose_clean.common.Result.BackendResult
import com.example.compose_clean.common.Result.DatabaseResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
@ExperimentalCoroutinesApi
class RestaurantRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val mapper: RestaurantResponseMapper,
    private val dao: RestaurantDao,
    private val restaurantApi: RestaurantApi,
    private val cityApi: CityApi
) {

    private lateinit var latestSelectedCity: String
    private var latestSearch = ""
    private var resultIsFromBackend = false

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

    // todo: might need tweaks, see how it's handling when back is pressed while on restaurant details screen
    // todo: 0 error handling make this flow for collecting only, and differetnt fun for api calls
    suspend fun restaurantDetails(id: String): FlowResult<RestaurantEntity> = callbackFlow {
        Timber.d("Fetching restaurant details for id $id")
        var tablesResponse: List<TableResponse>? = null
        var reservationsResponse: List<ReservationResponse>? = null
        resultIsFromBackend = false
        val job = CoroutineScope(Dispatchers.IO).launch {
            dao.data(id).collectLatest {
                Timber.d("Collected restaurant details $it")
                if (resultIsFromBackend) {
                    trySendBlockingExt(BackendResult(it))
                } else {
                    trySendBlockingExt(DatabaseResult(it))
                }
            }
        }
        withContext(Dispatchers.IO) {
            launch {
                Timber.d("Fetching restaurant tables")
                tablesResponse = restaurantApi.getRestaurantTables(id)
                Timber.d("Got restaurant tables $tablesResponse")
            }
            launch {
                Timber.d("Fetching restaurant reservations")
                reservationsResponse = restaurantApi.getRestaurantReservations(id)
                Timber.d("Got restaurant reservations $reservationsResponse")
            }
        }
        Timber.d("Finished fetching restaurant details, updating entity")
        resultIsFromBackend = true
        val tables = mapper.tableResponseListToTables(tablesResponse)
        val reservations = mapper.reservationResponseListToReservations(reservationsResponse)
        dao.updateDetails(id, tables, reservations)
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

            val restaurantsResponse = getRestaurants(city, search)
            val mappedResult = mapper.restaurantResponseListToEntities(restaurantsResponse)

            resultIsFromBackend = true
            val restaurantsWithoutImage = dao.addOrUpdateDetails(mappedResult)
            updateImagesFor(restaurantsWithoutImage)
            resultIsFromBackend = false
        }

    private suspend fun getRestaurants(
        city: String,
        search: String
    ): List<RestaurantResponse> {
        val result = restaurantApi.getRestaurants(city, search)
        Timber.d("Got restaurants from api $city, updating database")
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