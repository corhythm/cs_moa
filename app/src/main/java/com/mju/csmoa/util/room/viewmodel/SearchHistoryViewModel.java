package com.mju.csmoa.util.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mju.csmoa.util.room.entity.SearchHistory;
import com.mju.csmoa.util.room.repository.SearchHistoryRepository;

import java.util.List;

public class SearchHistoryViewModel extends AndroidViewModel {

    private final SearchHistoryRepository searchHistoryRepository;
    private final LiveData<List<SearchHistory>> searchHistoryList;

    public SearchHistoryViewModel(@NonNull Application application) {
        super(application);
        searchHistoryRepository = new SearchHistoryRepository(application);
        searchHistoryList = searchHistoryRepository.getSearchHistoryList();
    }

    public LiveData<List<SearchHistory>> getSearchHistoryList() {
        return searchHistoryList;
    }

    public void insert(SearchHistory searchHistory) {
        searchHistoryRepository.insert(searchHistory);
    }
}
