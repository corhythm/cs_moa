package com.mju.csmoa.home.event_item.viewholder

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.DetailEventItemActivity
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.util.Constants.TAG

class RecommendedEventItemViewHolder(
    private val context: Context,
    private val itemRecommendedEventItemBinding: ItemRecommendedEventItemBinding
) :
    RecyclerView.ViewHolder(itemRecommendedEventItemBinding.root) {

    fun bind(itemEventItem: ItemEventItem) {
        Log.d(TAG, "RecommendedEventItemViewHolder -bind() called / itemEventItem: $itemEventItem")
        // 상품 이름
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemItemName.text =
            itemEventItem.itemName
        // 상품 가격
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemItemPrice.text =
            "${itemEventItem.itemPrice}원"
        // 상품 실질 가격
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemItemActualPrice.text =
            "(개당 ${itemEventItem.itemActualPrice}원)"
        // 상품 조회수
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemViewCount.text =
            itemEventItem.viewCount.toString()
        // 상품 좋아요 개수
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemLikeCount.text =
            itemEventItem.likeCount.toString()
        // root view backgroundTintColor
        itemEventItem.colorCode?.let {
            itemRecommendedEventItemBinding.cardViewItemRecommendedEventItemRootContainer.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(itemEventItem.colorCode))
        }


        Glide.with(context).load(itemEventItem.itemImageSrc)
            .placeholder(R.drawable.img_all_itemimage)
            .error(R.drawable.ic_all_big_x)
            .into(itemRecommendedEventItemBinding.imageViewItemRecommendedEventItemRecommendedImage)

        // 이벤트 타입별 컬러 설정
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
        itemRecommendedEventItemBinding.cardViewItemRecommendedEventItemCsBrandContainer.strokeColor =
            csBrandStrokeColor
        itemRecommendedEventItemBinding.imageViewItemRecommendedEventItemCsBrand.setImageResource(
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
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemEventType.text =
            itemEventItem.itemEventType
        itemRecommendedEventItemBinding.textViewItemRecommendedEventItemEventType.setTextColor(
            eventTypeColor
        )
        itemRecommendedEventItemBinding.cardViewItemRecommendedEventItemEventTypeContainer.strokeColor =
            eventTypeColor

        // root 아이템 클릭했을 때
        itemRecommendedEventItemBinding.root.setOnClickListener {
            val detailEventItemIntent = Intent(context, DetailEventItemActivity::class.java).apply {
                putExtra("itemEventItem", itemEventItem)
            }
            context.startActivity(detailEventItemIntent)
        }
    }
}