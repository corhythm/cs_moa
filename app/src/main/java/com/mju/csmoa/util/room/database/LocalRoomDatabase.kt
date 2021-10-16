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

//    private class LocalRoomDatabaseCallback(private val scope: CoroutineScope) :
//        RoomDatabase.Callback() {
//
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    val searchHistoryDao = database.searchHistoryDao()
//
//                    searchHistoryDao.deleteAllSearchHistory()
//                }
//            }
//        }
//    }

    companion object {

        @Volatile
        private var INSTANCE: LocalRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope? = null): LocalRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        LocalRoomDatabase::class.java, "csmoa_local_db"
                    ).build();
//                    Room.databaseBuilder(
//                    context.applicationContext,
//                    LocalRoomDatabase::class.java,
//                    "csmoa_local_db")
//                    .addMigrations(MIGRATION_1_2)
////                    .addCallback(LocalRoomDatabaseCallback(scope)) // 지금 당장에 사용 안 함. 패턴 정도만 익히자.
//                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        // migrate 할 때 사용, 근데 그냥 콘솔로 들어가서 내부 DB 다 지우면 됨
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            // From version 1 to version 2
            override fun migrate(database: SupportSQLiteDatabase) {
                // Remove the table
                database.execSQL("DROP TABLE SearchHistory") // Use the right table name

                // OR: We could update it, by using an ALTER query

                // OR: If needed, we can create the table again with the required settings
                // database.execSQL("CREATE TABLE IF NOT EXISTS my_table (id INTEGER, PRIMARY KEY(id), ...)")
            }
        }
    }
}