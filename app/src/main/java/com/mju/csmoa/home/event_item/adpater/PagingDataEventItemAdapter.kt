package com.mju.csmoa.home.event_item.adpater

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.EventItemViewHolder

class EventItemPagingDataAdapter(private val eventItemChangedListener: EventItemChangedListener) :
    PagingDataAdapter<EventItem, EventItemViewHolder>(DiffUtilCallback()){

    companion object {
        const val HEADER = 0 // 추천 아이템
        const val BODY = 1 // 행사 상품
    }

    override fun getItemViewType(position: Int) = BODY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemViewHolder {
        return EventItemViewHolder(parent, eventItemChangedListener)
    }

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        holder.bind(getItem(position))
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




