package com.mju.csmoa.home.event_item.viewholder.nested_viewholder

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemRecommendedEventItemListBinding
import com.mju.csmoa.home.event_item.adpater.RecommendedEventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.util.RecyclerViewDecoration

// 추천 행사 상품 리사이클러뷰 뷰홀더
class RecommendedEventItemListViewHolder(
    private val context: Context,
    private val itemRecommendedEventItemListBinding: ItemRecommendedEventItemListBinding
) :
    RecyclerView.ViewHolder(itemRecommendedEventItemListBinding.root) {

    fun setRecommendedEventItemList(recommendedEventItemList: List<ItemEventItem>) {

        val recommendedEventItemRecyclerAdapter = RecommendedEventItemRecyclerAdapter(context, recommendedEventItemList)

        itemRecommendedEventItemListBinding.recyclerViewItemRecommendedEventItemListRecommendedItems.apply {
            adapter = recommendedEventItemRecyclerAdapter
            addItemDecoration(RecyclerViewDecoration(0, 0, 0, 50))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }


}