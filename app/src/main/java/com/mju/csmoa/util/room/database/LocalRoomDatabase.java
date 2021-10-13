package com.mju.csmoa.util.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mju.csmoa.util.room.dao.SearchHistoryDao;
import com.mju.csmoa.util.room.entity.SearchHistory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

@Database(entities = {SearchHistory.class}, version = 2, exportSchema = false)
public abstract class LocalRoomDatabase extends RoomDatabase {

    @Getter
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static volatile LocalRoomDatabase INSTANCE;

    // abstract method
    public abstract SearchHistoryDao getSearchHistoryDao();

    public static LocalRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalRoomDatabase.class) {
                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            LocalRoomDatabase.class, "local_database")
//                            .build();
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalRoomDatabase.class, "database-name")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    // migrate 할 때 사용, 근데 그냥 콘솔로 들어가서 내부 DB 다 지우면 됨
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) { // From version 1 to version 2
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Remove the table
            database.execSQL("DROP TABLE SearchHistory"); // Use the right table name

            // OR: We could update it, by using an ALTER query

            // OR: If needed, we can create the table again with the required settings
            // database.execSQL("CREATE TABLE IF NOT EXISTS my_table (id INTEGER, PRIMARY KEY(id), ...)")
        }
    };

}
