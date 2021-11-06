package com.mju.csmoa.home.event_item.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.domain.model.AllEventItem
import com.mju.csmoa.home.event_item.viewholder.EventItemViewHolder
import com.mju.csmoa.home.event_item.viewholder.nested_viewholder.RecommendedEventItemListViewHolder

class MainRecyclerAdapter(
    private val allEventItemList: List<AllEventItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER = 0 // 추천 아이템
        const val BODY = 1 // 행사 상품
    }

    override fun getItemViewType(position: Int): Int {
        return allEventItemList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> RecommendedEventItemListViewHolder(parent)
            BODY -> EventItemViewHolder(parent)
            else -> throw IllegalStateException("Unknown View")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            // 추천 행사 상품 리스트
            is RecommendedEventItemListViewHolder -> holder.setRecommendedEventItemList(
                allEventItemList[position].recommendedEventItemList ?: ArrayList()
            )
            is EventItemViewHolder -> holder.bind(itemEventItem = allEventItemList[position].eventItem!!)
        }
    }

    override fun getItemCount(): Int = allEventItemList.size
}




