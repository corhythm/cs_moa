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
        // ?????? ????????? ????????? -> ?????? ??????
        binding.toolbarDetailEventItemToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // ?????? ?????? ????????????
        var eventItemId: Long = -1
        if (intent.hasExtra("eventItemId")) {
            eventItemId = intent.getLongExtra("eventItemId", -1)
            type = intent.getIntExtra("type", -1) // recommendEventItem?????? normalEventItem??????
            position = intent.getIntExtra("position", -1) // item absolute position
            Log.d(TAG, "In DetailEventItemActivity, eventItemId = $eventItemId, position = $position, type = $type")
        }

        if (eventItemId == (-1).toLong() || type == -1 || position == -1) {
            makeToast("?????? ?????? ??????", "???????????? ???????????? ??? ??????????????????", MotionToastStyle.ERROR)
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
                    intent.flags = FLAG_ACTIVITY_CLEAR_TOP // ??? ??? ???????????? ????????????
                    startActivity(intent)
                }

                // ????????? ??????????????? ????????????
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

                            // ?????? ?????? ?????? ???????????? init
                            detailEventItem = response.result!!.detailEventItem
                            withContext(Dispatchers.Main) { initEventItemInfo(detailEventItem!!) }
                        }
                        else -> {
                            withContext(Dispatchers.Main) {
                                makeToast("?????? ?????? ??????", "???????????? ???????????? ??? ??????????????????", MotionToastStyle.ERROR)
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
                    makeToast("?????? ?????? ??????", "???????????? ???????????? ??? ??????????????????", MotionToastStyle.ERROR)
                }
            }
        }
    }

    private fun initEventItemInfo(detailEventItem: EventItem) {

        with(binding) {
            // ????????? ????????????
            Glide.with(this@DetailEventItemActivity)
                .load(detailEventItem.itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(imageViewDetailEventItemItemImage)

            textViewDetailEventItemItemName.text = detailEventItem.itemName // ????????? ??????
            textViewDetailEventItemItemPrice.text = detailEventItem.itemPrice // ????????? ??????
            textViewDetailEventItemItemActualPrice.text =
                detailEventItem.itemActualPrice // ????????? ?????? ??????
            textViewDetailEventItemViewCount.text = detailEventItem.viewCount.toString() // ?????????
            textViewDetailEventItemLikeCount.text = detailEventItem.likeCount.toString() // ????????? ??????

            if (detailEventItem.isLike!!) { // ????????? ?????????
                lottieAnimationViewDetailEventItemHeart.progress = 0.5f
            }

            // like ?????? ????????? ???
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

                    //????????? ?????? ????????????
                    textViewDetailEventItemLikeCount.text = detailEventItem.likeCount.toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        val accessToken =
                            MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                        val response: BaseResponse<PostEventItemLikeRes>? =
                            RetrofitManager.retrofitService?.postEventItemLike(
                                accessToken!!,
                                PostEventItemHistoryAndLikeReq(detailEventItem.eventItemId!!)
                            )

                        // ?????? ??????????????? ??? ????????? ?????? ?????????
                        if (response == null || response.code != 100 || response.result == null) {
                            throw IOException("Network Error")
                        }
                        Log.d(TAG, "response = $response")
                    }
                } catch (exception: Exception) {
                    Log.d(TAG, "${exception.printStackTrace()}")
                    makeToast("?????????", "???????????? ??? ??? ????????????", MotionToastStyle.ERROR)
                }
            }

            // ????????? ???????????? => ????????? <-> ?????????
            lottieAnimationViewDetailEventItemHeart.setOnClickListener(likeSetOnClickListener)
            textViewDetailEventItemLikeCount.setOnClickListener(likeSetOnClickListener)

            // ????????? ????????? ??????
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
                setText("????????? ?????? ????????? ?????? ?????????????")
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
                        title = "??????!!!",
                        message = "?????? ??????????????? ?????? ????????? ?????? ?????? ????????? :(",
                        buttonAText = "??????",
                        buttonBText = "??????",
                        onButtonAClicked = { },
                        ouButtonBClicked = {  // Map?????? ??????
                            // Map?????? ??????
                            startActivity(
                                Intent(
                                    this@DetailEventItemActivity,
                                    CSMapActivity::class.java
                                ).apply {
                                    putExtra("csBrand", detailEventItem.csBrand) // ????????? ????????? ?????? ??????
                                })
                        }
                    ).show()
                })
                setBalloonAnimation(BalloonAnimation.FADE)
                setLifecycleOwner(lifecycleOwner)
            }

            // ????????? ????????? ???????????? -> ????????? ??????
            binding.imageViewDetailEventItemCsBrand.setOnClickListener {
                goToMapBalloon.showAlignBottom(binding.imageViewDetailEventItemCsBrand)
            }

            // ????????? ?????? ??????
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

    // ????????? ???, eventItem ?????? ????????????
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