package com.mju.csmoa.home.event_item.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.EventItem

class DetailEventItemViewHolder(
    parent: ViewGroup
) :
    RecyclerView.ViewHolder(
        ItemDetailRecommendedEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemDetailRecommendedEventItemBinding.bind(itemView)

    fun bind(eventItem: EventItem) {

        with(binding) {
            // 제품 이미지 불러오기
            Glide.with(root.context).load(eventItem.itemImageSrc)
                .placeholder(R.drawable.img_all_itemimage)
                .error(R.drawable.ic_all_big_x)
                .into(imageViewItemDetailRecommendedEventItemItemImage)

            // 행사 제품 이름
            textViewItemDetailRecommendedEventItemItemName.text = eventItem.itemName
            // 행사 제품 가격
            textViewItemDetailRecommendedEventItemItemPrice.text = eventItem.itemPrice
            // 행사 제품 1개당 가격
            textViewItemDetailRecommendedEventItemItemActualPrice.text =
                eventItem.itemActualPrice

            // 조회수랑, 좋아요 개수는 가져오지 않음

            // csbrand
            val csBrandColorList =
                root.context.resources.getStringArray(R.array.cs_brand_color_list)
            var csBrandStrokeColor = Color.BLACK
            var csBrandResourceId = -1
            when (eventItem.csBrand) {
                "cu" -> {
                    csBrandStrokeColor = Color.parseColor(csBrandColorList[0])
                    csBrandResourceId = R.drawable.img_cs_cu
                }
                "gs25" -> {
                    csBrandStrokeColor = Color.parseColor(csBrandColorList[1])
                    csBrandResourceId = R.drawable.img_cs_gs25
                }
                "seven" -> {
                    csBrandStrokeColor = Color.parseColor(csBrandColorList[2])
                    csBrandResourceId = R.drawable.img_cs_seveneleven
                }
                "ministop" -> {
                    csBrandStrokeColor = Color.parseColor(csBrandColorList[3])
                    csBrandResourceId = R.drawable.img_cs_ministop
                }
                "emart24" -> {
                    csBrandStrokeColor = Color.parseColor(csBrandColorList[4])
                    csBrandResourceId = R.drawable.img_cs_emart24
                }
            }

            // 편의점 브랜드 설정
            cardViewItemDetailRecommendedEventItemEventTypeContainer.strokeColor =
                csBrandStrokeColor
            imageViewItemDetailRecommendedEventItemCsBrand.setImageResource(csBrandResourceId)


            val eventTypeColorList =
                root.context.resources.getStringArray(R.array.event_type_color_list)
            var eventTypeColor = Color.BLACK
            when (eventItem.itemEventType) {
                "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
                "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
                "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
                "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
            }

            // 이벤트 타입 설정
            textViewItemDetailRecommendedEventItemEventType.text = eventItem.itemEventType
            textViewItemDetailRecommendedEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemDetailRecommendedEventItemEventTypeContainer.strokeColor = eventTypeColor
        }


    }


}