package com.mju.csmoa.util.room.dao

import androidx.room.*
import com.mju.csmoa.util.room.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM SearchHistory order by createdAt desc;")
    fun getSearchHistoriesFlow(): Flow<List<SearchHistory>>

    @Query("DELETE FROM SearchHistory")
    suspend fun deleteAllSearchHistory()
}