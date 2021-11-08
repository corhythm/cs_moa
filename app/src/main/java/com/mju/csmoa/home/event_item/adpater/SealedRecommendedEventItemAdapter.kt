package com.mju.csmoa.home.event_item.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.viewholder.SealedRecommendedEventItemViewHolder

class SealedRecommendedEventItemAdapter(private val recommendedEventItemList: List<EventItem>) :
    RecyclerView.Adapter<SealedRecommendedEventItemViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return HEADER
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SealedRecommendedEventItemViewHolder = SealedRecommendedEventItemViewHolder(parent)


    override fun onBindViewHolder(holder: SealedRecommendedEventItemViewHolder, position: Int) {
        holder.setRecommendedEventItemList(recommendedEventItemList)
    }

    override fun getItemCount() = 1
}