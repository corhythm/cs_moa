package com.mju.csmoa.home.event_item.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.DetailEventItemViewHolder

class DetailRecommendedEventItemAdapter(
    private val detailRecommendedEventItemList: List<EventItem>,
    private val onDetailedRecommendedEventItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<DetailEventItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DetailEventItemViewHolder(parent, onDetailedRecommendedEventItemClicked)

    override fun onBindViewHolder(holder: DetailEventItemViewHolder, position: Int) {
        holder.bind(detailRecommendedEventItemList[position])
    }

    override fun getItemCount() = detailRecommendedEventItemList.size
}