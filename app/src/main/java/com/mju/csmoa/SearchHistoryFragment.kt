package com.mju.csmoa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.FragmentSearchHistoryBinding
import com.mju.csmoa.databinding.ItemSearchHistoryBinding
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.entity.SearchHistory
import com.mju.csmoa.util.room.viewmodel.SearchHistoryViewModel
import java.util.*

class SearchHistoryFragment : Fragment(), RemoveButtonClickListener {
    private var binding: FragmentSearchHistoryBinding? = null
    private var searchHistoryViewModel: SearchHistoryViewModel? = null
    private val TAG = "로그"
    private var searchHistoryAdapter: SearchHistoryAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchHistoryBinding.inflate(inflater, container, false)
        init()
        return binding!!.root
    }

    private fun init() {

        // init recyclerView
//        List<SearchHistory> searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = SearchHistoryAdapter()
        searchHistoryAdapter!!.setRemoveButtonClickListener(this)
        binding!!.recyclerViewRecentSearchSearchList.adapter = searchHistoryAdapter
        binding!!.recyclerViewRecentSearchSearchList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 최근 검색어 전부 삭제
        binding!!.textViewSearchHistoryClearAll.setOnClickListener { v: View? ->
            val searchHistoryDao = LocalRoomDatabase.getDatabase(context).searchHistoryDao
            LocalRoomDatabase.getDatabaseWriteExecutor().execute { searchHistoryDao.deleteAllSearchHistory() }
            // 최근 검색어 데이터 양이 많지 않으므로 notifyDataSetChanged() 호출해도 오버헤드가 크지 않을 듯.
            searchHistoryAdapter!!.notifyDataSetChanged()
        }

        // ViewModel init
        searchHistoryViewModel = ViewModelProvider(this).get(SearchHistoryViewModel::class.java)
        searchHistoryViewModel!!.searchHistoryList.observe(viewLifecycleOwner, { searchHistoryList: List<SearchHistory> ->
            // update the cached copy of the searchHistories in the adapter.
            Log.d(TAG, "onChanged: 데이터 변경 감지")
            searchHistoryAdapter!!.submitList(searchHistoryList)
            searchHistoryAdapter!!.notifyDataSetChanged()
        })
    }

    override fun setOnClicked(searchHistory: SearchHistory?) {
        // remove specific search history
        val database = LocalRoomDatabase.getDatabase(context)
        LocalRoomDatabase.getDatabaseWriteExecutor().execute { database.searchHistoryDao.deleteSearchHistory(searchHistory) }
        // 이 코드를 위 스레드 스코프에서 실행하면 UI 스레드가 아니어서 앱 죽음
        searchHistoryAdapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

internal class SearchHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var searchHistoryList: List<SearchHistory> = ArrayList()
    private var removeButtonClickListener: RemoveButtonClickListener? = null
    fun submitList(searchHistoryList: List<SearchHistory>) {
        this.searchHistoryList = searchHistoryList
    }

    fun setRemoveButtonClickListener(removeButtonClickListener: RemoveButtonClickListener?) {
        this.removeButtonClickListener = removeButtonClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val searchHistoryViewHolder = SearchHistoryViewHolder(
                ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        searchHistoryViewHolder.setRemoveButtonClickListener(removeButtonClickListener)
        return searchHistoryViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SearchHistoryViewHolder).bind(searchHistoryList[position])
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }
}

internal class SearchHistoryViewHolder(private val itemSearchHistoryBinding: ItemSearchHistoryBinding) : RecyclerView.ViewHolder(itemSearchHistoryBinding.root) {
    private var removeButtonClickListener: RemoveButtonClickListener? = null
    fun setRemoveButtonClickListener(removeButtonClickListener: RemoveButtonClickListener?) {
        this.removeButtonClickListener = removeButtonClickListener
    }

    fun bind(searchHistory: SearchHistory) {
        itemSearchHistoryBinding.textViewItemRecentSearchSearchWord.text = searchHistory.searchWord
        itemSearchHistoryBinding.textViewItemRecentSearchDate.text = searchHistory.createdAt
        Log.d("로그", "bind: " + searchHistory.searchHistoryId + ", " + searchHistory.searchWord + ", " + searchHistory.createdAt)
        // 특정 검색어 삭제 버튼을 눌렀을 때
        itemSearchHistoryBinding.imageViewItemRecentSearchRemove.setOnClickListener { v: View? -> removeButtonClickListener!!.setOnClicked(searchHistory) }
    }
}

internal interface RemoveButtonClickListener {
    fun setOnClicked(searchHistory: SearchHistory?)
}