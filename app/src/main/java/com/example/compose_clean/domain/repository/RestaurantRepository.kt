package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.containsExt
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.data.DataStoreManager
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.data.mapper.RestaurantMapper
import com.example.compose_clean.ui.view.states.GenericResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// TODO: this should be interface, impl is in data layer
@ExperimentalCoroutinesApi
class RestaurantRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val restaurantDao: RestaurantDao,
    private val restaurantApi: RestaurantApi,
    private val cityApi: CityApi
) : RestaurantMapper {

    companion object {
        const val DEFAULT_CITY = "Skopje"
        const val SELECTED_CITY = "SELECTED_CITY"
    }

    lateinit var latestSelectedCity: String
    var latestSearch = ""

    suspend fun restaurants(): Flow<List<RestaurantEntity>> {
        val selectedCity = getSelectedCity().first()
        latestSelectedCity = selectedCity
        val resturantsFlow = restaurantDao.dataFlow().flatMapLatest {
            // todo: search functionality should be extracted and refined
            flowOf(
                it.filter {
                    (it.name.containsExt(latestSearch) || it.type.containsExt(latestSearch)) && (it.city == latestSelectedCity)
                }
            )
        }
        return resturantsFlow
    }

    suspend fun refresh(city: String, search: String = ""): GenericResult<Unit> =
        safeResultWithContext(Dispatchers.IO) {
            latestSelectedCity = city
            latestSearch = search
            saveSelectedCity(city)
            val addedRestaurants = getRestaurantsDetails(city, search)
            getRestaurantsImages(addedRestaurants)
        }

    fun getSelectedCity(): Flow<String> = dataStoreManager.getSelectedCity()

    suspend fun getCities(): GenericResult<List<String>> = safeResultWithContext(Dispatchers.IO) {
        cityApi.getCities()
    }

    suspend fun saveSelectedCity(city: String) = dataStoreManager.saveSelectedCity(city)

    private suspend fun getRestaurantsDetails(
        city: String,
        search: String
    ): List<RestaurantEntity> {
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