package com.example.compose_clean.common.di

import com.example.compose_clean.domain.repository.AuthRepository
import com.example.compose_clean.domain.repository.RestaurantDetailsRepository
import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCase
import com.example.compose_clean.domain.usecase.restaurantdetails.GetRestaurantDetailsUseCaseImpl
import com.example.compose_clean.domain.usecase.restaurants.*
import com.example.compose_clean.domain.usecase.session.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetRestaurantsUseCase(
        restaurantRepository: RestaurantRepository
    ): GetRestaurantsUseCase {
        return GetRestaurantsUseCaseImpl(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideGetRestaurantDetailsUseCase(
        restaurantDetailsRepository: RestaurantDetailsRepository
    ): GetRestaurantDetailsUseCase {
        return GetRestaurantDetailsUseCaseImpl(restaurantDetailsRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshRestaurantsUseCase(
        restaurantRepository: RestaurantRepository
    ): RefreshRestaurantsUseCase {
        return RefreshRestaurantsUseCaseImpl(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideGetSelectedCityUseCase(
        restaurantRepository: RestaurantRepository
    ): GetSelectedCityUseCase {
        return GetSelectedCityUseCaseImpl(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideSaveSelectedCityUseCase(
        restaurantRepository: RestaurantRepository
    ): SaveSelectedCityUseCase {
        return SaveSelectedCityUseCaseImpl(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideGetCitiesUseCase(
        restaurantRepository: RestaurantRepository
    ): GetCitiesUseCase {
        return GetCitiesUseCaseImpl(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(
        authRepository: AuthRepository
    ): AuthUseCase {
        return AuthUseCaseImpl(authRepository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCaseImpl(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        authRepository: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCaseImpl(authRepository)
    }

}