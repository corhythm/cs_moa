package com.mju.csmoa.home.event_item

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityDetailEventItemBinding
import com.mju.csmoa.home.event_item.adpater.DetailRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration

class DetailEventItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventItemBinding
    private lateinit var eventItem: EventItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun init() {

        // 행사 제품 정보 설정
        initEventItemInfo()

        // 네비 아이콘 눌르면 -> 뒤로 가기
        binding.toolbarDetailEventItemToolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        RetrofitManager.instance.getEventItem(
            eventItemId = eventItem.eventItemId!!,
            completion = { statusCode, detailRecommendedEventList ->
                when (statusCode) {
                    100 -> {
                        Log.d(
                            TAG,
                            "DetailEventItemActivity -init() called / detailRecommendedEventList = $detailRecommendedEventList"
                        )
                        val detailRecommendedEventItemRecyclerAdapter =
                            DetailRecommendedEventItemAdapter(detailRecommendedEventList!!)

                        // init recyclerView
                        binding.recyclerViewDetailEventItemRecommendList.apply {
                            adapter = detailRecommendedEventItemRecyclerAdapter
                            layoutManager = GridLayoutManager(
                                this@DetailEventItemActivity,
                                2, GridLayoutManager.HORIZONTAL, false
                            )
                            addItemDecoration(RecyclerViewDecoration(0, 50, 0, 0))
                        }
                    }
                    else -> {
                        Log.d(TAG, "no respond")
                    }
                }

            })


    }

    private fun initEventItemInfo() {
        // 만약 Parcelable 객체가 있으면
        if (intent.hasExtra("itemEventItem")) {
            eventItem = intent.getParcelableExtra("itemEventItem")!!

            with(binding) {
                // 이미지 가져오기
                Glide.with(this@DetailEventItemActivity).load(eventItem.itemImageSrc)
                    .placeholder(R.drawable.img_all_itemimage)
                    .error(R.drawable.ic_all_big_x)
                    .into(imageViewDetailEventItemItemImage)

                textViewDetailEventItemItemName.text = eventItem.itemName
                textViewDetailEventItemItemPrice.text = eventItem.itemPrice
                textViewDetailEventItemItemActualPrice.text = eventItem.itemActualPrice

                // csbrand
                var csBrandResourceId = -1
                when (eventItem.csBrand) {
                    "cu" -> csBrandResourceId = R.drawable.img_cs_cu
                    "gs25" -> csBrandResourceId = R.drawable.img_cs_gs25
                    "seven" -> csBrandResourceId = R.drawable.img_cs_seveneleven
                    "ministop" -> csBrandResourceId = R.drawable.img_cs_ministop
                    "emart24" -> csBrandResourceId = R.drawable.img_cs_emart24
                }
                // 편의점 브랜드 설정
                binding.imageViewDetailEventItemCsBrand.setImageResource(csBrandResourceId)

                val eventTypeColorList = resources.getStringArray(R.array.event_type_color_list)
                var eventTypeColor = Color.BLACK
                when (eventItem.itemEventType) {
                    "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
                    "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
                    "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
                    "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
                }

                // 이벤트 타입 설정
                textViewDetailEventItemEventType.text = eventItem.itemEventType
                textViewDetailEventItemEventType.setTextColor(eventTypeColor)
                cardViewDetailEventItemEventTypeContainer.strokeColor = eventTypeColor
            }


        }

    }
}