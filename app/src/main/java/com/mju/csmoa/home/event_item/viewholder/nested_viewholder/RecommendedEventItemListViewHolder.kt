package com.mju.csmoa.home.event_item.viewholder.nested_viewholder

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemRecommendedEventItemListBinding
import com.mju.csmoa.home.event_item.adpater.RecommendedEventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem

// 추천 행사 상품 리사이클러뷰 뷰홀더
class RecommendedEventItemListViewHolder(
    private val context: Context,
    private val itemRecommendedEventItemListBinding: ItemRecommendedEventItemListBinding
) :
    RecyclerView.ViewHolder(itemRecommendedEventItemListBinding.root) {

//    private lateinit var recommendedEventItemList: List<ItemEventItem>


    fun setRecommendedEventItemList(recommendedEventItemList: List<ItemEventItem>) {
//        this.recommendedEventItemList = recommendedEventItemList

        val recommendedEventItemRecyclerAdapter = RecommendedEventItemRecyclerAdapter(context, recommendedEventItemList)
        itemRecommendedEventItemListBinding.recyclerViewItemRecommendedEventItemListRecommendedItems.apply {
            adapter = recommendedEventItemRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }


}