package com.example.compose_clean.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("CleanComposePrefs")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    companion object {
        const val DEFAULT_CITY = "Skopje"
        val SELECTED_CITY = stringPreferencesKey("selected_city")
    }

    private val dataStore = appContext.dataStore

    fun getSelectedCity(): Flow<String> = dataStore.data.map {
        it[SELECTED_CITY] ?: DEFAULT_CITY
    }

    suspend fun saveSelectedCity(city: String) {
        dataStore.edit {
            it[SELECTED_CITY] = city
        }
    }
}