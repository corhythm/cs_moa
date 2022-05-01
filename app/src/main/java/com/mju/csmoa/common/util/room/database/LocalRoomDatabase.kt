package com.mju.csmoa.common.util.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mju.csmoa.common.util.room.dao.SearchHistoryDao
import com.mju.csmoa.common.util.room.entity.SearchHistory
import kotlinx.coroutines.CoroutineScope

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