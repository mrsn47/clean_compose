package com.example.compose_clean.common.di

import com.example.compose_clean.data.api.CityApi
import com.example.compose_clean.data.api.RestaurantApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideRestaurantApi(
    ): RestaurantApi {
        return RestaurantApi()
    }

    @Provides
    @Singleton
    fun provideCityApi(
    ): CityApi {
        return CityApi()
    }

}