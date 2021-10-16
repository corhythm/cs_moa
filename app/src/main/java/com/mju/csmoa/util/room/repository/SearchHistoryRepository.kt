package com.mju.csmoa.util.room.repository

import com.mju.csmoa.util.room.dao.SearchHistoryDao
import com.mju.csmoa.util.room.entity.SearchHistory
import kotlinx.coroutines.flow.Flow


class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    val allSearchHistories: Flow<List<SearchHistory>> = searchHistoryDao.getSearchHistoriesFlow()

    suspend fun insert(searchHistory: SearchHistory) {
        searchHistoryDao.insertSearchHistory(searchHistory)
    }
}