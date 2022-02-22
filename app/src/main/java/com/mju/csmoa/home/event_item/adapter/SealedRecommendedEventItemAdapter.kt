package com.mju.csmoa.home.event_item.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.SealedRecommendedEventItemViewHolder

class SealedRecommendedEventItemAdapter(
    private val recommendedEventItemList: List<EventItem>,
    private val eventItemChangedListener: EventItemChangedListener
) :
    RecyclerView.Adapter<SealedRecommendedEventItemViewHolder>() {

    override fun getItemViewType(position: Int) = HEADER

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    )= SealedRecommendedEventItemViewHolder(parent, eventItemChangedListener)


    override fun onBindViewHolder(holder: SealedRecommendedEventItemViewHolder, position: Int) {
        holder.setRecommendedEventItemList(recommendedEventItemList)
    }

    override fun getItemCount() = 1
}