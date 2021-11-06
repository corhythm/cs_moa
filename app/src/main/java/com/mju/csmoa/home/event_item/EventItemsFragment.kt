package com.mju.csmoa.home.event_item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.home.event_item.adpater.MainRecyclerAdapter
import com.mju.csmoa.home.event_item.adpater.MainRecyclerAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.adpater.MainRecyclerAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.domain.model.AllEventItem
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration

class EventItemsFragment : Fragment() {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!
    private val allEventItemList = ArrayList<AllEventItem>()

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

        // 맨 위로 클릭했을 때
        binding.cardViewItemRecommendedEventGotoTop.setOnClickListener {
            binding.recyclerViewEventItemsRecommendationEventItems.smoothScrollToPosition(0)
        }

        // 필터 버튼 클릭했을 때때
        binding.cardViewItemRecommendedEventEventTypeContainer.setOnClickListener {
            FilteringBottomSheetDialog(requireContext()).show()
        }

        RetrofitManager.instance.getEventItems { statusCode, getEventItemsRes ->
            when (statusCode) {
                // allItemEvent 서버에서 받아오기
                100 -> {
                    val colorList = requireContext().resources.getStringArray(R.array.color_top10)

                    // set ColorCodeList
                    getEventItemsRes?.recommendedEventItemList?.forEachIndexed { index, itemEventItem ->
                        itemEventItem.colorCode = colorList[index]
                    }

                    allEventItemList.add(
                        AllEventItem(
                            type = HEADER,
                            getEventItemsRes?.recommendedEventItemList as ArrayList<ItemEventItem>,
                            null
                        )
                    )

                    getEventItemsRes.eventItemList.forEach { itemEventItem ->
                        allEventItemList.add(
                            AllEventItem(
                                type = BODY,
                                null,
                                itemEventItem
                            )
                        )
                    }

                    val mainRecyclerAdapter = MainRecyclerAdapter(allEventItemList)

                    binding.recyclerViewEventItemsRecommendationEventItems.apply {
                        layoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.VERTICAL,
                            false
                        ).apply {
                            spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    Log.d(
                                        TAG,
                                        "mainRecyclerAdapter.getItemViewType(position) = ${
                                            mainRecyclerAdapter.getItemViewType(position)
                                        }"
                                    )
                                    return when (mainRecyclerAdapter.getItemViewType(position)) {
                                        HEADER -> 2
                                        BODY -> 1
                                        else -> -1
                                    }
                                }
                            })
                        }

                        addItemDecoration(RecyclerViewDecoration(0, 30, 10, 10))
                        adapter = mainRecyclerAdapter
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}