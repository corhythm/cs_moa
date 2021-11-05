package com.mju.csmoa.home.event_item.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemDetailRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.viewholder.DetailEventItemViewHolder

class DetailRecommendedEventItemRecyclerAdapter(
    private val context: Context,
    private val detailRecommendedEventItemList: List<ItemEventItem>
) : RecyclerView.Adapter<DetailEventItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailEventItemViewHolder {
        return DetailEventItemViewHolder(
            context = context,
            itemDetailRecommendedEventItemBinding = ItemDetailRecommendedEventItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailEventItemViewHolder, position: Int) {
        holder.bind(detailRecommendedEventItemList[position])
    }

    override fun getItemCount() = detailRecommendedEventItemList.size
}