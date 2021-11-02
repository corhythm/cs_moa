package com.mju.csmoa.home.event_item.viewholder

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.util.Constants.TAG

class EventItemViewHolder(
    private val context: Context,
    private val itemEventItemBinding: ItemEventItemBinding
) :
    RecyclerView.ViewHolder(itemEventItemBinding.root) {

    fun bind(itemEventItem: ItemEventItem) {
        itemEventItemBinding.textViewItemEventItemItemName.text = itemEventItem.itemName
        itemEventItemBinding.textViewItemEventItemPrice.text = itemEventItem.itemPrice
        itemEventItemBinding.textViewItemEventItemActualPrice.text = itemEventItem.itemActualPrice
        itemEventItemBinding.textViewItemEventItemViewCount.text =
            itemEventItem.viewCount.toString()
        itemEventItemBinding.textViewItemEventItemLikeCount.text =
            itemEventItem.likeCount.toString()


        Glide.with(context).load(itemEventItem.itemImageSrc)
            .placeholder(R.drawable.img_all_itemimage)
            .error(R.drawable.ic_all_big_x)
            .into(itemEventItemBinding.imageViewItemEventItemEventItemImage)



        Log.d(TAG, "EventItemViewHolder -bind() called / itemEventItem = $itemEventItem")

        itemEventItemBinding.root.setOnClickListener {


        }
    }
}