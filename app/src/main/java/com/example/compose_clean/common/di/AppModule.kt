package com.example.compose_clean.common.di

import android.content.Context
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.AppDatabase
import com.example.compose_clean.data.db.dao.RestaurantDao
import com.example.compose_clean.domain.repository.AuthRepository
import com.example.compose_clean.domain.repository.RestaurantRepository
import com.example.compose_clean.domain.usecase.restaurants.RefreshRestaurantsUseCase
import com.example.compose_clean.domain.usecase.restaurants.RestaurantsFlowUseCase
import com.example.compose_clean.domain.usecase.session.AuthUseCase
import com.example.compose_clean.domain.usecase.session.LoginUseCase
import com.example.compose_clean.domain.usecase.session.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Api
    @Provides
    @Singleton
    fun provideRestaurantApi(
    ): RestaurantApi {
        return RestaurantApi()
    }

    // Dao

    @Provides
    @Singleton
    fun provideRestaurantDao(
        @ApplicationContext appContext: Context
    ): RestaurantDao {
        return AppDatabase.getDatabase(appContext).restaurants()
    }

    // Repository

    @Provides
    @Singleton
    fun provideRestaurantRepository(
        restaurantDao: RestaurantDao,
        restaurantApi: RestaurantApi
    ): RestaurantRepository {
        return RestaurantRepository(restaurantDao, restaurantApi)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
    ): AuthRepository {
        return AuthRepository()
    }

    // Use Case

    @Provides
    @Singleton
    fun provideRestaurantsFlowUseCase(
        restaurantRepository: RestaurantRepository
    ): RestaurantsFlowUseCase {
        return RestaurantsFlowUseCase(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshRestaurantsUseCase(
        restaurantRepository: RestaurantRepository
    ): RefreshRestaurantsUseCase {
        return RefreshRestaurantsUseCase(restaurantRepository)
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(
        authRepository: AuthRepository
    ): AuthUseCase {
        return AuthUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        authRepository: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }
}