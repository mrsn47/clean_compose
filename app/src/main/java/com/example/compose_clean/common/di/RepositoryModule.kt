package com.example.compose_clean.common.di

import com.example.compose_clean.data.ConnectivityService
import com.example.compose_clean.data.DataStoreManager
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.data.mapper.RestaurantDetailsResponseMapper
import com.example.compose_clean.data.mapper.RestaurantResponseMapper
import com.example.compose_clean.domain.repository.AuthRepository
import com.example.compose_clean.domain.repository.RestaurantDetailsRepository
import com.example.compose_clean.domain.repository.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRestaurantRepository(
        dataStoreManager: DataStoreManager,
        restaurantResponseMapper: RestaurantResponseMapper,
        restaurantDao: RestaurantDao,
        restaurantApi: RestaurantApi,
        cityApi: CityApi
    ): RestaurantRepository {
        return RestaurantRepository(
            dataStoreManager,
            restaurantResponseMapper,
            restaurantDao,
            restaurantApi,
            cityApi
        )
    }

    @Provides
    @Singleton
    fun provideRestaurantDetailsRepository(
        connectivityService: ConnectivityService,
        restaurantDetailsResponseMapper: RestaurantDetailsResponseMapper,
        restaurantDao: RestaurantDao,
        restaurantApi: RestaurantApi,
        scope: CoroutineScope
    ): RestaurantDetailsRepository {
        return RestaurantDetailsRepository(
            connectivityService,
            restaurantDetailsResponseMapper,
            restaurantDao,
            restaurantApi,
            scope
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
    ): AuthRepository {
        return AuthRepository()
    }

}