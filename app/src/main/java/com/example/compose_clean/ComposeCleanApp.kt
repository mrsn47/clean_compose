package com.example.compose_clean

import android.app.Application
import com.example.compose_clean.data.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ComposeCleanApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(applicationContext)
    }
}