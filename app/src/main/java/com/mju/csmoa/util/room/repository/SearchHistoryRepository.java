package com.mju.csmoa.util.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mju.csmoa.util.room.dao.SearchHistoryDao;
import com.mju.csmoa.util.room.database.LocalRoomDatabase;
import com.mju.csmoa.util.room.entity.SearchHistory;

import java.util.List;

public class SearchHistoryRepository {

    private final SearchHistoryDao searchHistoryDao;
    private final LiveData<List<SearchHistory>> searchHistoryList;

    // Note that in order to unit test the WordRepository, you have to remove the Application dependency.
    // This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SearchHistoryRepository(Application application) {
        LocalRoomDatabase db = LocalRoomDatabase.getDatabase(application);
        searchHistoryDao = db.getSearchHistoryDao();
        searchHistoryList = searchHistoryDao.getSearchHistoryList();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<SearchHistory>> getSearchHistoryList() {
        return searchHistoryList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(SearchHistory searchHistory) {
        LocalRoomDatabase.getDatabaseWriteExecutor().execute(() -> searchHistoryDao.insertSearchHistory(searchHistory));
    }

}
