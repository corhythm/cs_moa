package com.mju.csmoa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.FragmentSearchHistoryBinding
import com.mju.csmoa.databinding.ItemSearchHistoryBinding
import com.mju.csmoa.util.MyApplication
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.entity.SearchHistory
import com.mju.csmoa.util.room.viewmodel.SearchHistoryViewModel
import com.mju.csmoa.util.room.viewmodel.SearchHistoryViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class SearchHistoryFragment : Fragment(), RemoveButtonClickListener {
    private var _binding: FragmentSearchHistoryBinding? = null
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels {
        SearchHistoryViewModelFactory(MyApplication.instance.repository)
    }
    private val TAG = "로그"
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchHistoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        // init recyclerView
        searchHistoryAdapter = SearchHistoryAdapter()
        searchHistoryAdapter.setRemoveButtonClickListener(this)

        binding.recyclerViewRecentSearchSearchList.apply {
            adapter = searchHistoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // ViewModel init
        // Add an observer on the LiveData returned by getSearchHistoriesFlow.
        // The onChanged() method fires when the observed data changes and the activity in the foreground.
        // SearchHistoryFragment is a LifeCycleOwner
        searchHistoryViewModel.allSearchHistories.observe(viewLifecycleOwner) { searchHistories ->
            Log.d(TAG, "SearchHistoryFragment.viewLifeCycleOwner: $viewLifecycleOwner")
            searchHistories.let {
                searchHistoryAdapter.submitList(searchHistories)
                searchHistoryAdapter.notifyDataSetChanged()
            }
        }

        // 최근 검색어 전부 삭제
        binding.textViewSearchHistoryClearAll.setOnClickListener {
            val database = LocalRoomDatabase.getDatabase(requireContext())
            lifecycleScope.launch {
                database.searchHistoryDao().deleteAllSearchHistory()
            }
            // 최근 검색어 데이터 양이 많지 않으므로 notifyDataSetChanged() 호출해도 오버헤드가 크지 않을 듯.
            searchHistoryAdapter.notifyDataSetChanged()
        }


    }

    override fun setOnClicked(searchHistory: SearchHistory) {
        // remove specific search history
        val database = LocalRoomDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            database.searchHistoryDao().deleteSearchHistory(searchHistory)
        }
        // 이 코드를 위 스레드 스코프에서 실행하면 UI 스레드가 아니어서 앱 죽음
        searchHistoryAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        Log.d(TAG, "SearchHistoryFragment.onDestroyView: ")
        super.onDestroyView()
        _binding = null
    }
}

internal class SearchHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var searchHistoryList: List<SearchHistory?>? = ArrayList()
    private lateinit var removeButtonClickListener: RemoveButtonClickListener

    fun submitList(searchHistoryList: List<SearchHistory?>?) {
        this.searchHistoryList = searchHistoryList
    }

    fun setRemoveButtonClickListener(removeButtonClickListener: RemoveButtonClickListener) {
        this.removeButtonClickListener = removeButtonClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val searchHistoryViewHolder = SearchHistoryViewHolder(
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        searchHistoryViewHolder.setRemoveButtonClickListener(removeButtonClickListener)
        return searchHistoryViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SearchHistoryViewHolder).bind(searchHistoryList!![position])
    }

    override fun getItemCount(): Int {
        return searchHistoryList!!.size
    }
}

internal class SearchHistoryViewHolder(private val itemSearchHistoryBinding: ItemSearchHistoryBinding) :
    RecyclerView.ViewHolder(itemSearchHistoryBinding.root) {

    private lateinit var removeButtonClickListener: RemoveButtonClickListener

    fun setRemoveButtonClickListener(removeButtonClickListener: RemoveButtonClickListener) {
        this.removeButtonClickListener = removeButtonClickListener
    }

    fun bind(searchHistory: SearchHistory?) {
        itemSearchHistoryBinding.textViewItemRecentSearchSearchWord.text =
            searchHistory!!.searchWord
        itemSearchHistoryBinding.textViewItemRecentSearchDate.text = searchHistory.createdAt

//        Log.d("로그", "bind: " + searchHistory.searchHistoryId + ", " + searchHistory.searchWord + ", " + searchHistory.createdAt);
        // 특정 검색어 삭제 버튼을 눌렀을 때
        itemSearchHistoryBinding.imageViewItemRecentSearchRemove.setOnClickListener {
            removeButtonClickListener.setOnClicked(searchHistory)
        }
    }
}

internal interface RemoveButtonClickListener {
    fun setOnClicked(searchHistory: SearchHistory)
}