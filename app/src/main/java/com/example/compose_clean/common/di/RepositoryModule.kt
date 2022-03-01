package com.example.compose_clean.common.di

import android.content.SharedPreferences
import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.domain.repository.AuthRepository
import com.example.compose_clean.domain.repository.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRestaurantRepository(
        sharedPreferences: SharedPreferences,
        restaurantDao: RestaurantDao,
        restaurantApi: RestaurantApi,
        cityApi: CityApi
    ): RestaurantRepository {
        return RestaurantRepository(sharedPreferences, restaurantDao, restaurantApi, cityApi)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
    ): AuthRepository {
        return AuthRepository()
    }

}