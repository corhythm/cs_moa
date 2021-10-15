package com.mju.csmoa.util

import android.app.Application
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.repository.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LocalRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SearchHistoryRepository(database.searchHistoryDao()) }

    companion object {
        lateinit var instance: MyApplication
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}