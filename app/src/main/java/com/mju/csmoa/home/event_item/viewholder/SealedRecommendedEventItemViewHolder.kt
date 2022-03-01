package com.mju.csmoa.home.event_item.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemSealedRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.adapter.RecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.common.util.RecyclerViewDecoration

// 추천 행사 상품 리사이클러뷰 뷰홀더
class SealedRecommendedEventItemViewHolder(
    parent: ViewGroup,
    private val eventItemChangedListener: EventItemChangedListener
) :
    RecyclerView.ViewHolder(
        ItemSealedRecommendedEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemSealedRecommendedEventItemBinding.bind(itemView)

    init {
        binding.recyclerViewItemSealedRecommendedEventItemRecommendedItems.addItemDecoration(
            RecyclerViewDecoration(0, 0, 0, 30)
        )
    }

    fun setRecommendedEventItemList(recommendedEventItemList: List<EventItem>) {

        val recommendedEventItemAdapter =
            RecommendedEventItemAdapter(recommendedEventItemList, eventItemChangedListener)

        eventItemChangedListener.setRecommendedEventItemAdapter(recommendedEventItemAdapter)

        with(binding) {
            recyclerViewItemSealedRecommendedEventItemRecommendedItems.apply {
                adapter = recommendedEventItemAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


}