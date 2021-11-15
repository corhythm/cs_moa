package com.mju.csmoa.home.event_item.viewholder

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemEventItemBinding
import com.mju.csmoa.home.event_item.DetailEventItemActivity
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun bind(eventItem: EventItem?) {

        with(binding) {

            textViewItemEventItemItemName.text = eventItem?.itemName // 제품 이름
            textViewItemEventItemPrice.text = eventItem?.itemPrice // 제품 가격
            textViewItemEventItemActualPrice.text = eventItem?.itemActualPrice // 한 개당 가격
            textViewItemEventItemViewCount.text = eventItem?.viewCount.toString() // 제품 조회수
            textViewItemEventItemLikeCount.text = eventItem?.likeCount.toString() // 제품 좋아요 개수

            if (eventItem?.isLike!!) { // 좋아요 했으면
                imageViewItemEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
            }

            // 행사 제품 이미지 로딩
            Glide.with(root.context).load(eventItem?.itemImageSrc)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404) // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지를 설정한다.
                .fallback(R.drawable.ic_all_404) // load할 url이 null인 경우 등 비어있을 때 보여줄 이미지를 설정한다.
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .fitCenter()
                .into(imageViewItemEventItemEventItemImage)

            // csbrand
            val csBrandColorList =
                root.context.resources.getStringArray(R.array.cs_brand_color_list)
            var csBrandStrokeColor = Color.BLACK
            var csBrandResourceId = -1
            when (eventItem?.csBrand) {
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

            val eventTypeColorList =
                root.context.resources.getStringArray(R.array.event_type_color_list)
            var eventTypeColor = Color.BLACK
            when (eventItem?.itemEventType) {
                "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
                "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
                "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
                "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
            }

            // 이벤트 타입 설정
            textViewItemEventItemEventType.text = eventItem?.itemEventType
            textViewItemEventItemEventType.setTextColor(eventTypeColor)
            cardViewItemEventItemEventTypeContainer.strokeColor = eventTypeColor

            root.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val accessToken = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                    RetrofitManager.retrofitService?.postEventItemHistory(accessToken!!, PostEventItemHistoryAndLikeReq(eventItem.eventItemId!!))

                    launch(Dispatchers.Main) {
                        val detailEventItemIntent =
                            Intent(root.context, DetailEventItemActivity::class.java).apply {
                                putExtra("eventItemId", eventItem?.eventItemId)
                            }
                        root.context.startActivity(detailEventItemIntent)
                    }
                }
            }

        }


    }
}