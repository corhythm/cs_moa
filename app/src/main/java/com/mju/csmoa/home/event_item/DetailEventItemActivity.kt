package com.mju.csmoa.home.event_item

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.JwtTokenInfo
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityDetailEventItemBinding
import com.mju.csmoa.home.event_item.adpater.DetailRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.PostEventItemLikeRes
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.retrofit.common_domain.BaseResponse
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.log

class DetailEventItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventItemBinding
    private lateinit var jwtTokenInfo: JwtTokenInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 네비 아이콘 눌르면 -> 뒤로 가기
        binding.toolbarDetailEventItemToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // 행사 상품 PK 가져오기
        var eventItemId: Long = -1
        if (intent.hasExtra("eventItemId")) {
            eventItemId = intent.getLongExtra("eventItemId", -1)
        }
        if (eventItemId == (-1).toLong()) {
            makeToast("세부 행사 상품", "데이터를 받아오는 데 실패했습니다", MotionToastStyle.ERROR)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!
                val getDetailEventItemsRes =
                    RetrofitManager.retrofitService?.getDetailEventItem(
                        jwtTokenInfo.accessToken,
                        eventItemId = eventItemId
                    )

                // 데이터 정상적으로 받아오면
                if (getDetailEventItemsRes != null) {
                    when (getDetailEventItemsRes.code) {
                        100 -> {
                            val detailRecommendedEventItemRecyclerAdapter =
                                DetailRecommendedEventItemAdapter(getDetailEventItemsRes.result.detailRecommendedEventItems)

                            withContext(Dispatchers.Main) {
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
                            withContext(Dispatchers.Main) { initEventItemInfo(getDetailEventItemsRes.result.detailEventItem) }
                        }
                        else -> {
                            withContext(Dispatchers.Main) {
                                makeToast("세부 행사 상품", "데이터를 받아오는 데 실패했습니다", MotionToastStyle.ERROR)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.d(TAG, "DetailEventItemActivity -init() called (exception) $ex")
                Log.d(
                    TAG,
                    "DetailEventItemActivity -init() called / (exception) ${ex.printStackTrace()}"
                )
                withContext(Dispatchers.Main) {
                    makeToast("세부 행사 상품", "데이터를 받아오는 데 실패했습니다", MotionToastStyle.ERROR)
                }
            }
        }


    }

    private fun initEventItemInfo(detailEventItem: EventItem) {

        with(binding) {
            // 이미지 가져오기
            Glide.with(this@DetailEventItemActivity).load(detailEventItem.itemImageSrc)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(imageViewDetailEventItemItemImage)


            textViewDetailEventItemItemName.text = detailEventItem.itemName // 아이템 이름
            textViewDetailEventItemItemPrice.text = detailEventItem.itemPrice // 아이템 가격
            textViewDetailEventItemItemActualPrice.text =
                detailEventItem.itemActualPrice // 아이템 개당 가격
            textViewDetailEventItemViewCount.text = detailEventItem.viewCount.toString() // 조회수
            textViewDetailEventItemLikeCount.text = detailEventItem.likeCount.toString() // 좋아요 개수

            if (detailEventItem.isLike!!) { // 좋아요 했으면
                imageViewDetailEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
            }

            // 좋아요 클릭하면 => 좋아요 <-> 싫어요
            imageViewDetailEventItemHeart.setOnClickListener {
                try {
                    if (detailEventItem.isLike!!) {
                        detailEventItem.isLike = false
                        imageViewDetailEventItemHeart.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
                        detailEventItem.likeCount!!.minus(1)
                    } else {
                        detailEventItem.isLike = true
                        imageViewDetailEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
                        detailEventItem.likeCount!!.plus(1)
                    }
                    textViewDetailEventItemLikeCount.text = detailEventItem.likeCount.toString() // 좋아요 개수

                    CoroutineScope(Dispatchers.IO).launch {
                        val accessToken =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                        val response: BaseResponse<PostEventItemLikeRes>? =
                            RetrofitManager.retrofitService?.postEventItemLike(
                                accessToken!!,
                                PostEventItemHistoryAndLikeReq(detailEventItem.eventItemId!!)
                            )
                        Log.d(TAG, "response = $response")

                        // lottie heart
//                        launch(Dispatchers.Main) {
//                            if (response != null && response.isSuccess && response.result != null) {
//                                when (response.result!!.isLike) {
//                                    true -> {
//                                        imageViewDetailEventItemHeart.setImageResource(R.drawable.ic_all_filledheart)
//                                        detailEventItem.likeCount!!.plus(1)
//                                    }
//                                    false -> {
//                                        imageViewDetailEventItemHeart.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
//                                        detailEventItem.likeCount!!.minus(1)
//                                    }
//                                }
//                            }
//                        }
                    }
                } catch (exception: Exception) {
                    Log.d(TAG, "${exception.printStackTrace()}")
                    makeToast("좋아요", "좋아요를 할 수 없습니다", MotionToastStyle.ERROR)
                }
            }

            // csbrand
            var csBrandResourceId = -1
            when (detailEventItem.csBrand) {
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
            when (detailEventItem.itemEventType) {
                "1+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[0])
                "2+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[1])
                "3+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[2])
                "4+1" -> eventTypeColor = Color.parseColor(eventTypeColorList[3])
            }

            // 이벤트 타입 설정
            textViewDetailEventItemEventType.text = detailEventItem.itemEventType
            textViewDetailEventItemEventType.setTextColor(eventTypeColor)
            cardViewDetailEventItemEventTypeContainer.strokeColor = eventTypeColor
        }
    }

    private fun makeToast(title: String, content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            title,
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

}