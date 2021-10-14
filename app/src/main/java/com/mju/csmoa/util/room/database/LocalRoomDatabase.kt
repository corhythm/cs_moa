package com.mju.csmoa.util.room.database

import android.content.Context
import androidx.room.Database
import com.mju.csmoa.util.room.entity.SearchHistory
import androidx.room.RoomDatabase
import com.mju.csmoa.util.room.dao.SearchHistoryDao
import java.util.concurrent.ExecutorService
import kotlin.jvm.Volatile
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import lombok.Getter
import java.util.concurrent.Executors

@Database(entities = [SearchHistory::class], version = 2, exportSchema = false)
abstract class LocalRoomDatabase : RoomDatabase() {
    // abstract method
    abstract val searchHistoryDao: SearchHistoryDao?

    companion object {
        @Getter
        private val databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

        @Volatile
        private var INSTANCE: LocalRoomDatabase? = null
        fun getDatabase(context: Context): LocalRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(LocalRoomDatabase::class.java) {
                    if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            LocalRoomDatabase.class, "local_database")
//                            .build();
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            LocalRoomDatabase::class.java,
                            "database-name"
                        )
                            .addMigrations(MIGRATION_1_2)
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        // migrate 할 때 사용, 근데 그냥 콘솔로 들어가서 내부 DB 다 지우면 됨
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
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