package com.mju.csmoa.home

import android.os.Bundle
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
import com.mju.csmoa.home.review.ReviewsFragment
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.room.database.LocalRoomDatabase
import com.mju.csmoa.common.util.room.entity.SearchHistory
import com.mju.csmoa.common.util.room.viewmodel.SearchHistoryViewModel
import com.mju.csmoa.common.util.room.viewmodel.SearchHistoryViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SearchHistoryFragment(private val nowFragment: Fragment) : Fragment() {

    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get() = _binding!!

    private val  searchHistoryViewModel: SearchHistoryViewModel by viewModels {
        SearchHistoryViewModelFactory(nowFragment, MyApplication.instance.repository)
    }
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

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

        if (nowFragment is ReviewsFragment)
            binding.textViewSearchHistoryRecentSearchLabel.text = "리뷰 최근 검색어"
        else
            binding.textViewSearchHistoryRecentSearchLabel.text = "레시피 최근 검색어"

        // 특정 검색어 삭제
        val onDeleteClicked: (position: Int) -> Unit = {
            // remove specific search history
            val searchHistory = searchHistoryViewModel.allSearchHistories.value!![it]
            val database = LocalRoomDatabase.getDatabase(requireContext())
            lifecycleScope.launch(Dispatchers.IO) {
                database.searchHistoryDao().deleteSearchHistory(searchHistory)
            }
        }

        // 검색어 클릭했을 때
        val onSearchWordClicked: (position: Int) -> Unit = {
            val searchHistory = searchHistoryViewModel.allSearchHistories.value!![it]
            (requireActivity() as HomeActivity).goToSearchResults(searchHistory.searchWord)
        }

        // init recyclerView
        searchHistoryAdapter = SearchHistoryAdapter(onSearchWordClicked, onDeleteClicked)

        binding.recyclerViewRecentSearchSearchList.apply {
            adapter = searchHistoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // observing
        searchHistoryViewModel.allSearchHistories.observe(viewLifecycleOwner) { searchHistories ->
            searchHistories.let {
                searchHistoryAdapter.submitList(searchHistories)
                // 성능 부분에서 좀 손해를 보겠으나, 이 정도는 괜찮을 듯
                searchHistoryAdapter.notifyDataSetChanged()
            }
        }

        // 최근 검색어 전부 삭제
        binding.textViewSearchHistoryClearAll.setOnClickListener {
            val database = LocalRoomDatabase.getDatabase(requireContext())
            lifecycleScope.launch(Dispatchers.IO) {
                if (nowFragment is ReviewsFragment)
                    database.searchHistoryDao().deleteAllReviewSearchHistory()
                else
                    database.searchHistoryDao().deleteAllRecipeSearchHistory()
            }
        }
    }

    // 최근 검색 기록 스크롤 맨 위로
    override fun onResume() {
        val lineaLayoutManager: LinearLayoutManager =
            binding.recyclerViewRecentSearchSearchList.layoutManager as LinearLayoutManager
        lineaLayoutManager.scrollToPositionWithOffset(0, 0)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SearchHistoryAdapter(
    private val onSearchWordClicked: (position: Int) -> Unit,
    private val onDeleteClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<SearchHistoryViewHolder>() {

    private var searchHistoryList: List<SearchHistory> = ArrayList()

    fun submitList(searchHistoryList: List<SearchHistory>) {
        this.searchHistoryList = searchHistoryList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = SearchHistoryViewHolder(parent, onSearchWordClicked, onDeleteClicked)

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(searchHistoryList[position])
    }

    override fun getItemCount() = searchHistoryList.size
}

class SearchHistoryViewHolder(
    parent: ViewGroup,
    onSearchWordClicked: (position: Int) -> Unit,
    onDeleteClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemSearchHistoryBinding.bind(super.itemView)

    init {
        // 특정 검색어 삭제 버튼을 눌렀을 때
        binding.imageViewItemRecentSearchRemove.setOnClickListener {
            onDeleteClicked(absoluteAdapterPosition)
        }
        // 특정 검색어 눌렀을 때 -> 검색된 페이지로 이동
        binding.root.setOnClickListener { onSearchWordClicked(absoluteAdapterPosition) }
    }

    fun bind(searchHistory: SearchHistory) {
        binding.textViewItemRecentSearchSearchWord.text = searchHistory.searchWord
        binding.textViewItemRecentSearchDate.text = searchHistory.createdAt.split(' ')[0]
    }
}