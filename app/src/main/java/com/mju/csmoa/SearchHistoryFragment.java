package com.mju.csmoa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mju.csmoa.databinding.FragmentSearchHistoryBinding;
import com.mju.csmoa.databinding.ItemSearchHistoryBinding;
import com.mju.csmoa.util.room.dao.SearchHistoryDao;
import com.mju.csmoa.util.room.database.LocalRoomDatabase;
import com.mju.csmoa.util.room.entity.SearchHistory;
import com.mju.csmoa.util.room.viewmodel.SearchHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryFragment extends Fragment implements RemoveButtonClickListener{

    private FragmentSearchHistoryBinding binding;
    private SearchHistoryViewModel searchHistoryViewModel;
    private final String TAG = "로그";
    private SearchHistoryAdapter searchHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchHistoryBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

        // init recyclerView
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = new SearchHistoryAdapter();
        searchHistoryAdapter.setRemoveButtonClickListener(this);
        binding.recyclerViewRecentSearchSearchList.setAdapter(searchHistoryAdapter);
        binding.recyclerViewRecentSearchSearchList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // 최근 검색어 전부 삭제
        binding.textViewSearchHistoryClearAll.setOnClickListener(v -> {
            SearchHistoryDao searchHistoryDao = LocalRoomDatabase.getDatabase(getContext()).getSearchHistoryDao();
            LocalRoomDatabase.getDatabaseWriteExecutor().execute(searchHistoryDao::deleteAllSearchHistory);
            // 최근 검색어 데이터 양이 많지 않으므로 notifyDataSetChanged() 호출해도 오버헤드가 크지 않을 듯.
            searchHistoryAdapter.notifyDataSetChanged();
        });

        // ViewModel init
        searchHistoryViewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);
        searchHistoryViewModel.getSearchHistoryList().observe(getViewLifecycleOwner(), searchHistoryList -> {
            // update the cached copy of the searchHistories in the adapter.
            Log.d(TAG, "onChanged: 데이터 변경 감지");
            searchHistoryAdapter.submitList(searchHistoryList);
            searchHistoryAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void setOnClicked(SearchHistory searchHistory) {
        // remove specific search history
        LocalRoomDatabase database = LocalRoomDatabase.getDatabase(getContext());
        LocalRoomDatabase.getDatabaseWriteExecutor().execute(() -> {
            database.getSearchHistoryDao().deleteSearchHistory(searchHistory);
        });
        // 이 코드를 위 스레드 스코프에서 실행하면 UI 스레드가 아니어서 앱 죽음
        searchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "SearchHistoryFragment.onDestroyView: ");
        super.onDestroyView();
        binding = null;
    }
}


class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SearchHistory> searchHistoryList = new ArrayList<>();
    private RemoveButtonClickListener removeButtonClickListener;

    public void submitList(List<SearchHistory> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    public void setRemoveButtonClickListener(RemoveButtonClickListener removeButtonClickListener) {
        this.removeButtonClickListener = removeButtonClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchHistoryViewHolder searchHistoryViewHolder = new SearchHistoryViewHolder(
                ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        searchHistoryViewHolder.setRemoveButtonClickListener(removeButtonClickListener);
        return searchHistoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SearchHistoryViewHolder) holder).bind(searchHistoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }
}

class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
    private final ItemSearchHistoryBinding itemSearchHistoryBinding;
    private RemoveButtonClickListener removeButtonClickListener;

    public SearchHistoryViewHolder(@NonNull ItemSearchHistoryBinding itemSearchHistoryBinding) {
        super(itemSearchHistoryBinding.getRoot());
        this.itemSearchHistoryBinding = itemSearchHistoryBinding;
    }

    public void setRemoveButtonClickListener(RemoveButtonClickListener removeButtonClickListener) {
        this.removeButtonClickListener = removeButtonClickListener;
    }

    void bind(SearchHistory searchHistory) {
        itemSearchHistoryBinding.textViewItemRecentSearchSearchWord.setText(searchHistory.searchWord);
        itemSearchHistoryBinding.textViewItemRecentSearchDate.setText(searchHistory.createdAt);

//        Log.d("로그", "bind: " + searchHistory.searchHistoryId + ", " + searchHistory.searchWord + ", " + searchHistory.createdAt);
        // 특정 검색어 삭제 버튼을 눌렀을 때
        itemSearchHistoryBinding.imageViewItemRecentSearchRemove.setOnClickListener(v -> {
            removeButtonClickListener.setOnClicked(searchHistory);
        });
    }
}

interface RemoveButtonClickListener {
    void setOnClicked(SearchHistory searchHistory);
}

