package com.mju.csmoa.home.event_item.viewholder

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemEventItemBinding
import com.mju.csmoa.home.event_item.DetailEventItemActivity
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem

class EventItemViewHolder(
    parent: ViewGroup
) :
    RecyclerView.ViewHolder(
        ItemEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemEventItemBinding.bind(itemView)

    fun bind(itemEventItem: ItemEventItem) {

        with (binding) {

            // 제품 이름
            textViewItemEventItemItemName.text = itemEventItem.itemName
            // 제품 가격
            textViewItemEventItemPrice.text = "${itemEventItem.itemPrice}원"
            // 제품 실제 가격
            textViewItemEventItemActualPrice.text = "(개당 ${itemEventItem.itemActualPrice}원)"
            // 제품 조회수
            textViewItemEventItemViewCount.text = itemEventItem.viewCount.toString()
            // 제품 좋아요 개수
            textViewItemEventItemLikeCount.text = itemEventItem.likeCount.toString()


            Glide.with(root.context).load(itemEventItem.itemImageSrc)
                .placeholder(R.drawable.img_all_itemimage)
                .error(R.drawable.ic_all_big_x)
                .into(imageViewItemEventItemEventItemImage)

            // csbrand
            val csBrandColorList = root.context.resources.getStringArray(R.array.cs_brand_color_list)
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
            cardViewItemEventItemCsBrandContainer.strokeColor = csBrandStrokeColor
            imageViewItemEventItemCsBrand.setImageResource(csBrandResourceId)

            val eventTypeColorList = root.context.resources.getStringArray(R.array.event_type_color_list)
            var eventTypeColor = Color.BLACK
            when (itemEventItem.itemEventType) {
                "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
                "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
                "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
                "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
            }

            // 이벤트 타입 설정
            textViewItemEventItemEventType.text = itemEventItem.itemEventType
            textViewItemEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemEventItemEventTypeContainer.strokeColor = eventTypeColor

            root.setOnClickListener {
                val detailEventItemIntent = Intent(root.context, DetailEventItemActivity::class.java).apply {
                    putExtra("itemEventItem", itemEventItem)
                }
                root.context.startActivity(detailEventItemIntent)
            }

        }


    }
}