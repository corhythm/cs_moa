package com.mju.csmoa.home.event_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.home.event_item.adpater.MainRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.GetEventItemsRes
import com.mju.csmoa.home.event_item.domain.model.AllEventItem
import com.mju.csmoa.home.event_item.domain.model.AllEventItem.Companion.EVENT_ITEM_TYPE
import com.mju.csmoa.home.event_item.domain.model.AllEventItem.Companion.RECOMMENDED_TYPE
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.retrofit.RetrofitManager

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

//        binding.fabEventItemsFilter.setOnClickListener {
//            FilteringBottomSheetDialog(requireContext()).show()
//        }


        RetrofitManager.instance.getEventItems { statusCode, getEventItemsRes ->
            when (statusCode) {
                100 -> {
                    // allItemEvent 서버에서 받아오기
                    allEventItemList.add(
                        AllEventItem(
                            type = RECOMMENDED_TYPE,
                            getEventItemsRes?.recommendedEventItemList as ArrayList<ItemEventItem>,
                            null
                        )
                    )
                    allEventItemList.add(
                        AllEventItem(
                            type = EVENT_ITEM_TYPE,
                            null,
                            getEventItemsRes.eventItemList as ArrayList<ItemEventItem>
                        )
                    )

                    val eventItemRecyclerAdapter =
                        MainRecyclerAdapter(
                            allEventItemList = allEventItemList,
                            context = requireContext()
                        )

                    binding.recyclerViewEventItemsRecommendationEventItems.apply {
                        layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        adapter = eventItemRecyclerAdapter
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