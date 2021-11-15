package com.mju.csmoa.home.event_item.viewholder

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.DiscretePathEffect
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecommendedEventItemBinding
import com.mju.csmoa.home.event_item.DetailEventItemActivity
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendedEventItemViewHolder(
    parent: ViewGroup
) :
    RecyclerView.ViewHolder(
        ItemRecommendedEventItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private var binding = ItemRecommendedEventItemBinding.bind(itemView)

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
            }

            // root view backgroundTintColor
            eventItem.colorCode?.let {
                cardViewItemRecommendedEventItemRootContainer.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(eventItem.colorCode))
            }

            Glide.with(root.context).load(eventItem.itemImageSrc)
                .placeholder(R.drawable.ic_all_loading)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fallback(R.drawable.ic_all_404)
                .error(R.drawable.ic_all_404)
                .into(imageViewItemRecommendedEventItemRecommendedImage)

            // 이벤트 타입별 컬러 설정
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
            cardViewItemRecommendedEventItemCsBrandContainer.strokeColor = csBrandStrokeColor
            imageViewItemRecommendedEventItemCsBrand.setImageResource(csBrandResourceId)

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
            textViewItemRecommendedEventItemEventType.text = eventItem.itemEventType
            textViewItemRecommendedEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemRecommendedEventItemEventTypeContainer.strokeColor = eventTypeColor

            // root 아이템 클릭했을 때
            root.setOnClickListener {
                // 히스토리 삽입
                CoroutineScope(Dispatchers.IO).launch {
                    val accessToken = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                    RetrofitManager.retrofitService?.postEventItemHistory(accessToken!!, PostEventItemHistoryAndLikeReq(eventItem.eventItemId!!))

                    launch(Dispatchers.Main) {
                        val detailEventItemIntent =
                            Intent(root.context, DetailEventItemActivity::class.java).apply {
                                putExtra("eventItemId", eventItem.eventItemId)
                            }
                        root.context.startActivity(detailEventItemIntent)
                    }
                }
            }


        }
    }

}