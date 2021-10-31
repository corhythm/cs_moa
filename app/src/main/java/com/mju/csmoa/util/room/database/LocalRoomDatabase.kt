package com.mju.csmoa.util.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mju.csmoa.util.room.dao.SearchHistoryDao
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.entity.SearchHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [SearchHistory::class], version = 2, exportSchema = false)
abstract class LocalRoomDatabase : RoomDatabase() {

    // abstract method
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: LocalRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope? = null): LocalRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        LocalRoomDatabase::class.java, "csmoa_local_db"
                    ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }

    }

}