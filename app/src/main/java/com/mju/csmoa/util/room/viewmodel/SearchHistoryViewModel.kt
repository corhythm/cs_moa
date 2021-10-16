package com.mju.csmoa.util.room.viewmodel

import androidx.lifecycle.*
import com.mju.csmoa.util.room.entity.SearchHistory
import com.mju.csmoa.util.room.repository.SearchHistoryRepository
import kotlinx.coroutines.launch

class SearchHistoryViewModel(private val repository: SearchHistoryRepository) : ViewModel() {

    val allSearchHistories: LiveData<List<SearchHistory>> =
        repository.allSearchHistories.asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(searchHistory: SearchHistory) = viewModelScope.launch {
        repository.insert(searchHistory)
    }
}

class SearchHistoryViewModelFactory(private val repository: SearchHistoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }

}
