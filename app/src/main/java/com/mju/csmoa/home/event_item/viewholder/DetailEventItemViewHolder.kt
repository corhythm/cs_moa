package com.mju.csmoa.home.event_item.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.common.util.MyApplication

class DetailEventItemViewHolder(
    parent: ViewGroup,
    onDetailedRecommendedEventItemClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemDetailRecommendedEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemDetailRecommendedEventItemBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener {
            onDetailedRecommendedEventItemClicked(
                absoluteAdapterPosition
            )
        }
    }

    fun bind(eventItem: EventItem) {

        with(binding) {
            // 제품 이미지 불러오기
            Glide.with(root.context).load(eventItem.itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .fallback(R.drawable.ic_all_404)
                .error(R.drawable.ic_all_404)
                .into(imageViewItemDetailRecommendedEventItemItemImage)

            // 행사 제품 이름
            textViewItemDetailRecommendedEventItemItemName.text = eventItem.itemName
            // 행사 제품 가격
            textViewItemDetailRecommendedEventItemItemPrice.text = eventItem.itemPrice
            // 행사 제품 1개당 가격
            textViewItemDetailRecommendedEventItemItemActualPrice.text =
                eventItem.itemActualPrice


            val eventTypeColor = MyApplication.getEventTypeColor(eventItem.itemEventType!!)
            imageViewItemDetailRecommendedEventItemCsBrand
                .setImageResource(MyApplication.getCsBrandResourceId(eventItem.csBrand!!))


            // 이벤트 타입 설정
            textViewItemDetailRecommendedEventItemEventType.text = eventItem.itemEventType
            textViewItemDetailRecommendedEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemDetailRecommendedEventItemEventTypeContainer.strokeColor = eventTypeColor
        }


    }


}