package com.example.compose_clean.common.di

import com.example.compose_clean.data.mapper.RestaurantDetailsResponseMapper
import com.example.compose_clean.data.mapper.RestaurantResponseMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResponseMapperModule {

    @Provides
    @Singleton
    fun provideRestaurantResponseMapper(
    ): RestaurantResponseMapper {
        return RestaurantResponseMapper()
    }

    @Provides
    @Singleton
    fun provideRestaurantDetailsResponseMapper(
    ): RestaurantDetailsResponseMapper {
        return RestaurantDetailsResponseMapper()
    }

}