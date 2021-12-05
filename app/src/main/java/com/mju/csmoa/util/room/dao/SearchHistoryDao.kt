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

//    @Query("SELECT * FROM SearchHistory ORDER BY createdAt DESC;")
//    fun getSearchHistoriesFlow(): Flow<List<SearchHistory>>

    // 리뷰 최근 검색어
    @Query("SELECT * FROM SearchHistory WHERE type = 0 ORDER BY createdAt DESC")
    fun getReviewSearchHistoriesFlow(): Flow<List<SearchHistory>>

    // 레시피 최근 검색어
    @Query("SELECT * FROM SearchHistory WHERE type = 1 ORDER BY createdAt DESC")
    fun getRecipeSearchHistoriesFlow(): Flow<List<SearchHistory>>

//    @Query("DELETE FROM SearchHistory")
//    suspend fun deleteAllSearchHistory()

    // 리뷰 최근 검색어 모두 삭제
    @Query("DELETE FROM SearchHistory WHERE type = 0")
    suspend fun deleteAllReviewSearchHistory()

    // 레시피 최근 검색어 모두 삭제
    @Query("DELETE FROM SearchHistory WHERE type = 1")
    suspend fun deleteAllRecipeSearchHistory()
}