package com.mju.csmoa.home.event_item.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.R
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.databinding.ItemEventItemBinding
import com.mju.csmoa.home.event_item.EventItemChangedListener
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.domain.model.EventItem

class EventItemViewHolder(
    parent: ViewGroup,
    private val eventItemChangedListener: EventItemChangedListener
) :
    RecyclerView.ViewHolder(
        ItemEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemEventItemBinding.bind(itemView)

    init {
        // 특정 아이템 클릭 시
        binding.root.setOnClickListener {
            Log.d(TAG, "viewHolder position = $absoluteAdapterPosition")
            eventItemChangedListener.onClickedEventItem(
                type = BODY,
                position = absoluteAdapterPosition
            )
        }
    }

    fun bind(eventItem: EventItem?) {
        with(binding) {
            textViewItemEventItemItemName.text = eventItem?.itemName // 제품 이름
            textViewItemEventItemPrice.text = eventItem?.itemPrice // 제품 가격
            textViewItemEventItemActualPrice.text = eventItem?.itemActualPrice // 한 개당 가격
            textViewItemEventItemViewCount.text = eventItem?.viewCount.toString() // 제품 조회수
            textViewItemEventItemLikeCount.text = eventItem?.likeCount.toString() // 제품 좋아요 개수

            if (eventItem?.isLike!!) { // 좋아요 했으면
                imageViewItemEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewItemEventItemHeart.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            // 행사 제품 이미지 로딩
            Glide.with(root.context).load(eventItem.itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404) // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지를 설정한다.
                .fallback(R.drawable.ic_all_404) // load할 url이 null인 경우 등 비어있을 때 보여줄 이미지를 설정한다.
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .fitCenter()
                .into(imageViewItemEventItemEventItemImage)

            // csbrand

            val csBrandStrokeColor = MyApplication.getCsBrandColor(eventItem.csBrand!!)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(eventItem.csBrand)

            // 편의점 브랜드 설정
            cardViewItemEventItemCsBrandContainer.strokeColor = csBrandStrokeColor
            imageViewItemEventItemCsBrand.setImageResource(csBrandResourceId)

            // 이벤트 타입 설정
            val eventTypeColor = MyApplication.getEventTypeColor(eventItem.itemEventType!!)
            textViewItemEventItemEventType.text = eventItem.itemEventType
            textViewItemEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemEventItemEventTypeContainer.strokeColor = eventTypeColor
        }


    }
}