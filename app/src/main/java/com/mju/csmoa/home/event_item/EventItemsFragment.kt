package com.mju.csmoa.home.event_item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.home.event_item.adpater.EventItemLoadStateAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.adpater.RecommendedEventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.home.event_item.paging.EventItemViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class EventItemsFragment : Fragment() {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!
    val pagingDataAdapter = EventItemPagingDataAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventItemsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

        CoroutineScope(Dispatchers.Main).launch {
            initRecyclerView()
            initViewModel()

            with(binding) {
                // 맨 위로 클릭했을 때
                cardViewItemRecommendedEventGotoTop.setOnClickListener {
                    recyclerViewEventItemsRecommendationEventItems.scrollToPosition(0)
                }

                // 필터 버튼 클릭했을 때때
                cardViewItemRecommendedEventEventTypeContainer.setOnClickListener {
                    FilteringBottomSheetDialog(requireContext()).show()
                }
            }
        }

    }

    private suspend fun initRecyclerView() {

        val normalEventList = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitManager.retrofitService?.getEventItemsTemp(1)
            response?.result?.recommendedEventItemList
        }

        val recommendEventItemAdapter =
            RecommendedEventItemRecyclerAdapter(normalEventList.await()!!)
        pagingDataAdapter.withLoadStateFooter(footer = EventItemLoadStateAdapter { pagingDataAdapter.refresh() })
        val concatAdapter = ConcatAdapter(pagingDataAdapter, recommendEventItemAdapter)


        // pagingDataAdapter init

        binding.recyclerViewEventItemsRecommendationEventItems.apply {
            addItemDecoration(RecyclerViewDecoration(0, 30, 10, 10))
            adapter = concatAdapter
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            ).apply {
                spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int) =
                        when (pagingDataAdapter.getItemViewType(position)) {
                            HEADER -> 2
                            BODY -> 1
                            else -> -1
                        }
                })
            }
        }


    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this).get(EventItemViewModel::class.java)
        lifecycleScope.launchWhenCreated {
            viewModel.getEventItems().collectLatest {
                pagingDataAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}