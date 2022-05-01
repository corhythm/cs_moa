package com.mju.csmoa.common.util.room.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.mju.csmoa.home.review.ReviewsFragment
import com.mju.csmoa.common.util.room.entity.SearchHistory
import com.mju.csmoa.common.util.room.repository.SearchHistoryRepository
import kotlinx.coroutines.launch

class SearchHistoryViewModel(
    nowFragment: Fragment,
    private val repository: SearchHistoryRepository
) : ViewModel() {

    val allSearchHistories: LiveData<List<SearchHistory>> =
        if (nowFragment is ReviewsFragment) repository.allReviewSearchHistories.asLiveData()
        else repository.allRecipeSearchHistories.asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(searchHistory: SearchHistory) = viewModelScope.launch {
        repository.insert(searchHistory)
    }
}

class SearchHistoryViewModelFactory(
    private val nowFragment: Fragment,
    private val repository: SearchHistoryRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchHistoryViewModel(nowFragment, repository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }

}
