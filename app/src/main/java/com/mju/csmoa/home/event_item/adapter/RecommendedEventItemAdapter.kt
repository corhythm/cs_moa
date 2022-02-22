package com.mju.csmoa.home.event_item.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.RecommendedEventItemViewHolder

class RecommendedEventItemAdapter(
    private val recommendedEventItemList: List<EventItem>,
    private val eventItemChangedListener: EventItemChangedListener
) :
    RecyclerView.Adapter<RecommendedEventItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RecommendedEventItemViewHolder(parent, eventItemChangedListener)


    override fun onBindViewHolder(holder: RecommendedEventItemViewHolder, position: Int) {
        holder.bind(recommendedEventItemList[position])
    }

    override fun getItemCount() = recommendedEventItemList.size
}
