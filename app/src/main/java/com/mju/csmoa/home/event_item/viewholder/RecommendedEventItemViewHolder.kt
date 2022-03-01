package com.mju.csmoa.home.event_item.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.common.util.MyApplication

class RecommendedEventItemViewHolder(
    parent: ViewGroup,
    private val eventItemChangedListener: EventItemChangedListener
) :
    RecyclerView.ViewHolder(
        ItemRecommendedEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private var binding = ItemRecommendedEventItemBinding.bind(itemView)

    init {
        // root 아이템 클릭했을 때
        binding.root.setOnClickListener {
            eventItemChangedListener.onClickedEventItem(
                HEADER,
                absoluteAdapterPosition
            )
        }
    }

    fun bind(eventItem: EventItem) {
        with(binding) {
            textViewItemRecommendedEventItemItemName.text = eventItem.itemName // 상품 이름
            textViewItemRecommendedEventItemItemPrice.text = eventItem.itemPrice // 상품 가격
            textViewItemRecommendedEventItemItemActualPrice.text =
                eventItem.itemActualPrice // 상품 실질 가격
            textViewItemRecommendedEventItemViewCount.text =
                eventItem.viewCount.toString() // 상품 조회수
            textViewItemRecommendedEventItemLikeCount.text =
                eventItem.likeCount.toString() // 상품 좋아요 개수

            if (eventItem.isLike!!) { // 좋아요 했으면
                imageViewItemRecommendedEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewItemRecommendedEventItemHeart.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            // root view backgroundTintColor
            eventItem.colorCode?.let {
                cardViewItemRecommendedEventItemRootContainer.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(eventItem.colorCode))
            }

            Glide.with(root.context).load(eventItem.itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fallback(R.drawable.ic_all_404)
                .error(R.drawable.ic_all_404)
                .into(imageViewItemRecommendedEventItemRecommendedImage)

            // 편의점 브랜드 설정
            val csBrandStrokeColor = MyApplication.getCsBrandColor(eventItem.csBrand!!)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(eventItem.csBrand)
            cardViewItemRecommendedEventItemCsBrandContainer.strokeColor = csBrandStrokeColor
            imageViewItemRecommendedEventItemCsBrand.setImageResource(csBrandResourceId)

            // 이벤트 타입 설정
            val eventTypeColor = MyApplication.getEventTypeColor(eventItem.itemEventType!!)
            textViewItemRecommendedEventItemEventType.text = eventItem.itemEventType
            textViewItemRecommendedEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemRecommendedEventItemEventTypeContainer.strokeColor = eventTypeColor
        }
    }

}