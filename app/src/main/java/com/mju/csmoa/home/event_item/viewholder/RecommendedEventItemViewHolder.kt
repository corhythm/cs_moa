package com.mju.csmoa.home.event_item.viewholder

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication

class RecommendedEventItemViewHolder(
    private val context: Context,
    private val itemRecommendedEventItemBinding: ItemRecommendedEventItemBinding
) :
    RecyclerView.ViewHolder(itemRecommendedEventItemBinding.root) {

    fun bind(itemEventItem: ItemEventItem) {
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemName.text =
            itemEventItem.itemName
        itemRecommendedEventItemBinding.textViewItemRecommendedEventPrice.text =
            itemEventItem.itemPrice
        itemRecommendedEventItemBinding.textViewItemRecommendedEventActualPrice.text =
            itemEventItem.itemActualPrice
        itemRecommendedEventItemBinding.textViewItemRecommendedEventViewCount.text =
            itemEventItem.viewCount.toString()
        itemRecommendedEventItemBinding.textViewItemRecommendedEventLikeCount.text =
            itemEventItem.likeCount.toString()
        Log.d(TAG, "RecommendedEventItemViewHolder -bind() called / itemEventItem = $itemEventItem")

        Glide.with(context).load(itemEventItem.itemImageSrc)
            .placeholder(R.drawable.img_all_itemimage)
            .error(R.drawable.ic_all_big_x)
            .into(itemRecommendedEventItemBinding.imageViewItemRecommendedEventItemEventItemImage)


//            when (itemEventItem.eventType) {
//                "1+1" -> {
//
//                }
//                "2+1" -> {
//
//                }
//                "3+1" -> {
//
//                }
//                "4+1" -> {
//
//                }
//            }
//
//            when (itemEventItem.csBrand) {
//
//            }
//
//            itemRecommendedEventItemBinding.root.setOnClickListener {
//
//            }
    }
}