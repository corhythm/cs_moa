package com.mju.csmoa.util.room.dao

import com.mju.csmoa.util.room.entity.SearchHistory
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchHistory(searchHistory: SearchHistory?)

    @Delete
    fun deleteSearchHistory(searchHistory: SearchHistory?)

    @get:Query("SELECT * FROM SearchHistory order by createdAt desc;")
    val searchHistoryList: LiveData<List<SearchHistory?>?>?

    @Query("DELETE FROM SearchHistory")
    fun deleteAllSearchHistory()
}