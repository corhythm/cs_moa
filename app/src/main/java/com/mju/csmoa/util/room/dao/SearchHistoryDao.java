package com.mju.csmoa.util.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mju.csmoa.util.room.entity.SearchHistory;

import java.util.List;

@Dao
public interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchHistory(SearchHistory searchHistory);

    @Delete
    void deleteSearchHistory(SearchHistory searchHistory);

    @Query("SELECT * FROM SearchHistory order by createdAt desc;")
    LiveData<List<SearchHistory>> getSearchHistoryList();

    @Query("DELETE FROM SearchHistory")
    void deleteAllSearchHistory();

}
