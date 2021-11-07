package com.mju.csmoa.home.event_item.viewholder.nested_viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemRecommendedEventItemListBinding
import com.mju.csmoa.home.event_item.adpater.RecommendedEventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.util.RecyclerViewDecoration

// 추천 행사 상품 리사이클러뷰 뷰홀더
class RecommendedEventItemListViewHolder(
    private val parent: ViewGroup
) :
    RecyclerView.ViewHolder(
        ItemRecommendedEventItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemRecommendedEventItemListBinding.bind(itemView)

    init {
        binding.recyclerViewItemRecommendedEventItemListRecommendedItems.addItemDecoration(
            RecyclerViewDecoration(0, 0, 0, 30)
        )
    }

    fun setRecommendedEventItemList(recommendedEventItemList: List<EventItem>) {

        val recommendedEventItemRecyclerAdapter =
            RecommendedEventItemRecyclerAdapter(recommendedEventItemList)

        with(binding) {
            recyclerViewItemRecommendedEventItemListRecommendedItems.apply {
                adapter = recommendedEventItemRecyclerAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


}