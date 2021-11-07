package com.mju.csmoa.home.event_item.adpater

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.domain.model.AllEventItem
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.EventItemViewHolder
import com.mju.csmoa.home.event_item.viewholder.nested_viewholder.RecommendedEventItemListViewHolder
import java.lang.ClassCastException

class EventItemPagingDataAdapter :
    PagingDataAdapter<EventItem, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    companion object {
        const val HEADER = 0 // 추천 아이템
        const val BODY = 1 // 행사 상품
    }

    override fun getItemViewType(position: Int): Int {
//        return allEventItemList[position].type
        return BODY
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
            is RecommendedEventItemListViewHolder -> holder.setRecommendedEventItemList(ArrayList<EventItem>())
            is EventItemViewHolder -> holder.bind(getItem(position))
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<EventItem>() {

    override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
        return oldItem.eventItemId == newItem.eventItemId
    }

    override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
        return oldItem.eventItemId == newItem.eventItemId &&
                oldItem.itemName == newItem.itemName
    }

}




