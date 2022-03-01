package com.example.compose_clean.common.di

import android.content.Context
import com.example.compose_clean.data.api.RestaurantApi
import com.example.compose_clean.data.db.AppDatabase
import com.example.compose_clean.data.db.dao.RestaurantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideRestaurantDao(
        @ApplicationContext appContext: Context
    ): RestaurantDao {
        return AppDatabase.getDatabase(appContext).restaurants()
    }

}