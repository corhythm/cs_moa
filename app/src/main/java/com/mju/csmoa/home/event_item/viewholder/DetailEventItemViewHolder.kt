package com.mju.csmoa.home.event_item.viewholder

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem

class DetailEventItemViewHolder(
    private val context: Context,
    private val itemDetailRecommendedEventItemBinding: ItemDetailRecommendedEventItemBinding
) :
    RecyclerView.ViewHolder(itemDetailRecommendedEventItemBinding.root) {

    fun bind(itemEventItem: ItemEventItem) {

        // 제품 이미지 불러오기
        Glide.with(context).load(itemEventItem.itemImageSrc)
            .placeholder(R.drawable.img_all_itemimage)
            .error(R.drawable.ic_all_big_x)
            .into(itemDetailRecommendedEventItemBinding.imageViewItemDetailRecommendedEventItemItemImage)

        // 행사 제품 이름
        itemDetailRecommendedEventItemBinding.textViewItemDetailRecommendedEventItemItemName.text =
            itemEventItem.itemName
        // 행사 제품 가격
        itemDetailRecommendedEventItemBinding.textViewItemDetailRecommendedEventItemItemPrice.text =
            "${itemEventItem.itemPrice}원"
        // 행사 제품 1개당 가격
        itemDetailRecommendedEventItemBinding.textViewItemDetailRecommendedEventItemItemActualPrice.text =
            "(개당 ${itemEventItem.itemActualPrice}원)"

        // 조회수랑, 좋아요 개수는 가져오지 않음

        // csbrand
        val csBrandColorList = context.resources.getStringArray(R.array.cs_brand_color_list)
        var csBrandStrokeColor = Color.BLACK
        var csBrandResourceId = -1
        when (itemEventItem.csBrand) {
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
        itemDetailRecommendedEventItemBinding.cardViewItemDetailRecommendedEventItemEventTypeContainer.strokeColor =
            csBrandStrokeColor
        itemDetailRecommendedEventItemBinding.imageViewItemDetailRecommendedEventItemCsBrand.setImageResource(
            csBrandResourceId
        )


        val eventTypeColorList = context.resources.getStringArray(R.array.event_type_color_list)
        var eventTypeColor = Color.BLACK
        when (itemEventItem.itemEventType) {
            "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
            "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
            "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
            "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
        }

        // 이벤트 타입 설정
        itemDetailRecommendedEventItemBinding.textViewItemDetailRecommendedEventItemEventType.text =
            itemEventItem.itemEventType
        itemDetailRecommendedEventItemBinding.textViewItemDetailRecommendedEventItemEventType.setTextColor(
            eventTypeColor
        )
        itemDetailRecommendedEventItemBinding.cardViewItemDetailRecommendedEventItemEventTypeContainer.strokeColor =
            eventTypeColor
    }


}