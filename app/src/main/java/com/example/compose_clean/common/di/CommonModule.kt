package com.example.compose_clean.common.di

import android.content.Context
import com.example.compose_clean.data.ConnectivityService
import com.example.compose_clean.data.ConnectivityServiceLogic
import com.example.compose_clean.data.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun provideApplicationCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideConnectivityService(
        @ApplicationContext context: Context
    ): ConnectivityService {
        return ConnectivityServiceLogic(context)
    }

}