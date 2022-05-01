package com.mju.csmoa.common.util.room.repository

import com.mju.csmoa.common.util.room.dao.SearchHistoryDao
import com.mju.csmoa.common.util.room.entity.SearchHistory
import kotlinx.coroutines.flow.Flow


class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    val allReviewSearchHistories: Flow<List<SearchHistory>> = searchHistoryDao.getReviewSearchHistoriesFlow()
    val allRecipeSearchHistories: Flow<List<SearchHistory>> = searchHistoryDao.getRecipeSearchHistoriesFlow()

    suspend fun insert(searchHistory: SearchHistory) {
        searchHistoryDao.insertSearchHistory(searchHistory)
    }
}