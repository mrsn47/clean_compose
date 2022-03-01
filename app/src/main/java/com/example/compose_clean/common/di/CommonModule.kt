package com.example.compose_clean.common.di

import android.content.Context
import android.content.SharedPreferences
import com.example.compose_clean.data.api.RestaurantApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    private const val sharedPreferencesName = "CleanComposePrefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            sharedPreferencesName, Context.MODE_PRIVATE
        )
    }

}