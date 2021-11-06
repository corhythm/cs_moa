package com.mju.csmoa.home.event_item.adpater

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.home.event_item.viewholder.RecommendedEventItemViewHolder

class RecommendedEventItemRecyclerAdapter(
    private val recommendedEventItemList: List<ItemEventItem>
) :
    RecyclerView.Adapter<RecommendedEventItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RecommendedEventItemViewHolder(parent)


    override fun onBindViewHolder(holder: RecommendedEventItemViewHolder, position: Int) {
        holder.bind(recommendedEventItemList[position])
    }

    override fun getItemCount() = recommendedEventItemList.size


}
