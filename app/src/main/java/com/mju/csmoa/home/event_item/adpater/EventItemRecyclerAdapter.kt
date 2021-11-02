package com.mju.csmoa.home.event_item.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.viewholder.EventItemViewHolder

class EventItemRecyclerAdapter(
    private val context: Context,
    private val eventItemList: List<ItemEventItem>
) :
    RecyclerView.Adapter<EventItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemViewHolder {
        return EventItemViewHolder(
            context = context,
            itemEventItemBinding = ItemEventItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        holder.bind(eventItemList[position])
    }

    override fun getItemCount() = eventItemList.size
}