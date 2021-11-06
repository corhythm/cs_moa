package com.mju.csmoa.home.event_item.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.viewholder.DetailEventItemViewHolder

class DetailRecommendedEventItemRecyclerAdapter(
    private val detailRecommendedEventItemList: List<ItemEventItem>
) : RecyclerView.Adapter<DetailEventItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DetailEventItemViewHolder(parent)

    override fun onBindViewHolder(holder: DetailEventItemViewHolder, position: Int) {
        holder.bind(detailRecommendedEventItemList[position])
    }

    override fun getItemCount() = detailRecommendedEventItemList.size
}