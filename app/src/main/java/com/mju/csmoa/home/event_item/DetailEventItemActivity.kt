package com.mju.csmoa.home.event_item

import android.animation.ValueAnimator
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mju.csmoa.JwtTokenInfo
import com.mju.csmoa.R
import com.mju.csmoa.common.EitherAOrBDialog
import com.mju.csmoa.databinding.ActivityDetailEventItemBinding
import com.mju.csmoa.home.cs_location.CSMapActivity
import com.mju.csmoa.home.event_item.adapter.DetailRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.PostEventItemLikeRes
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.retrofit.common_domain.BaseResponse
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import com.skydoves.balloon.*
import kotlinx.coroutines.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.IOException

class DetailEventItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventItemBinding
    private lateinit var jwtTokenInfo: JwtTokenInfo
    private var detailEventItem: EventItem? = null
    private var type = -1 // HEADER(recommended, 0) || BODY(normal, 1)
    private var position = -1
    private lateinit var detailedRecommendedEventItems: List<EventItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        // 네비 아이콘 눌르면 -> 뒤로 가기
        binding.toolbarDetailEventItemToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // 행사 상품 가져오기
        var eventItemId: Long = -1
        if (intent.hasExtra("eventItemId")) {
            eventItemId = intent.getLongExtra("eventItemId", -1)
            type = intent.getIntExtra("type", -1) // recommendEventItem인지 normalEventItem인지
            position = intent.getIntExtra("position", -1) // item absolute position
            Log.d(TAG, "In DetailEventItemActivity, eventItemId = $eventItemId, position = $position, type = $type")
        }

        if (eventItemId == (-1).toLong() || type == -1 || position == -1) {
            makeToast("세부 행사 상품", "데이터를 받아오는 데 실패했습니다", MotionToastStyle.ERROR)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!
                val response =
                    RetrofitManager.retrofitService?.getDetailEventItem(
                        jwtTokenInfo.accessToken,
                        eventItemId = eventItemId
                    )

                val onDetailedRecommendedEventItemClicked: (position: Int) -> Unit = {
                    Log.d(TAG, "clicked = ${detailedRecommendedEventItems[it]}")
                    intent.putExtra("eventItemId", detailedRecommendedEventItems[it].eventItemId)
                    intent.flags = FLAG_ACTIVITY_CLEAR_TOP // 맨 위 액티비티 제거하고
                    startActivity(intent)
                }

                // 데이터 정상적으로 받아오면
                if (response?.code != null || response?.result != null) {
                    when (response.code) {
                        100 -> {
                            detailedRecommendedEventItems = response.result!!.detailRecommendedEventItems

                            val detailRecommendedEventItemRecyclerAdapter =
                                DetailRecommendedEventItemAdapter(
                                    detailedRecommendedEventItems,
                                    onDetailedRecommendedEventItemClicked
                                )

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

                            // 행사 상품 정보 넘겨주고 init
                            detailEventItem = response.result!!.detailEventItem
                            withContext(Dispatchers.Main) { initEventItemInfo(detailEventItem!!) }
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
            Glide.with(this@DetailEventItemActivity)
                .load(detailEventItem.itemImageUrl)
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
                lottieAnimationViewDetailEventItemHeart.progress = 0.5f
            }

            // like 버튼 눌렀을 떄
            val likeSetOnClickListener = View.OnClickListener {
                try {
                    val animator: ValueAnimator?

                    if (detailEventItem.isLike!!) {
                        detailEventItem.isLike = false
                        detailEventItem.likeCount = detailEventItem.likeCount!! - 1
                        animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(0L)
                    } else {
                        detailEventItem.isLike = true
                        detailEventItem.likeCount = detailEventItem.likeCount!! + 1
                        animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(1500L)
                    }

                    animator?.addUpdateListener { animation: ValueAnimator ->
                        lottieAnimationViewDetailEventItemHeart.progress =
                            animation.animatedValue as Float
                    }
                    animator?.start()

                    //좋아요 개수 업데이트
                    textViewDetailEventItemLikeCount.text = detailEventItem.likeCount.toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        val accessToken =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                        val response: BaseResponse<PostEventItemLikeRes>? =
                            RetrofitManager.retrofitService?.postEventItemLike(
                                accessToken!!,
                                PostEventItemHistoryAndLikeReq(detailEventItem.eventItemId!!)
                            )

                        // 만약 정상적으로 값 처리가 되지 않으면
                        if (response == null || response.code != 100 || response.result == null) {
                            throw IOException("Network Error")
                        }
                        Log.d(TAG, "response = $response")
                    }
                } catch (exception: Exception) {
                    Log.d(TAG, "${exception.printStackTrace()}")
                    makeToast("좋아요", "좋아요를 할 수 없습니다", MotionToastStyle.ERROR)
                }
            }

            // 좋아요 클릭하면 => 좋아요 <-> 싫어요
            lottieAnimationViewDetailEventItemHeart.setOnClickListener(likeSetOnClickListener)
            textViewDetailEventItemLikeCount.setOnClickListener(likeSetOnClickListener)

            // 편의점 브랜드 설정
            binding.imageViewDetailEventItemCsBrand
                .setImageResource(MyApplication.getCsBrandResourceId(detailEventItem.csBrand!!))

            // balloon
            val goToMapBalloon = createBalloon(this@DetailEventItemActivity) {
                setArrowSize(10)
                setWidth(BalloonSizeSpec.WRAP)
                setHeight(65)
                setPadding(10)
                setArrowPosition(0.7f)
                setCornerRadius(4f)
                setAutoDismissDuration(2500)
                setAlpha(0.9f)
                setText("가까운 주변 편의점 보러 가실래요?")
                setTextColorResource(R.color.white)
                setTextIsHtml(true)
                setIconDrawable(
                    ContextCompat.getDrawable(
                        this@DetailEventItemActivity,
                        R.drawable.ic_all_place
                    )
                )
                setBackgroundColorResource(R.color.balloon_color)
                setOnBalloonClickListener(OnBalloonClickListener {
                    EitherAOrBDialog(
                        context = this@DetailEventItemActivity,
                        theme = R.style.BottomSheetDialogTheme,
                        lottieName = "map2.json",
                        title = "주의!!!",
                        message = "주변 편의점에는 해당 상품이 없을 수도 있어요 :(",
                        buttonAText = "취소",
                        buttonBText = "확인",
                        onButtonAClicked = { },
                        ouButtonBClicked = {  // Map으로 이동
                            // Map으로 이동
                            startActivity(
                                Intent(
                                    this@DetailEventItemActivity,
                                    CSMapActivity::class.java
                                ).apply {
                                    putExtra("csBrand", detailEventItem.csBrand) // 편의점 브랜드 가치 전송
                                })
                        }
                    ).show()
                })
                setBalloonAnimation(BalloonAnimation.FADE)
                setLifecycleOwner(lifecycleOwner)
            }

            // 편의점 브랜드 클릭하면 -> 맵으로 이동
            binding.imageViewDetailEventItemCsBrand.setOnClickListener {
                goToMapBalloon.showAlignBottom(binding.imageViewDetailEventItemCsBrand)
            }

            // 이벤트 타입 설정
            val eventTypeColor = MyApplication.getEventTypeColor(detailEventItem.itemEventType!!)
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

    // 종료할 때, eventItem 정보 가져가기
    override fun onBackPressed() {
        if (detailEventItem != null) {
            val detailEventItemIntent = Intent().apply {
                putExtra("detailEventItem", detailEventItem)
                putExtra("type", this@DetailEventItemActivity.type)
                putExtra("position", position)
            }
            setResult(RESULT_OK, detailEventItemIntent)
            super.onBackPressed()
        }

    }

}