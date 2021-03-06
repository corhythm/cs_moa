package com.mju.csmoa.home.review

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.common.EitherAOrBDialog
import com.mju.csmoa.databinding.ActivityDetailedReviewBinding
import com.mju.csmoa.home.cs_location.CSMapActivity
import com.mju.csmoa.home.review.adapter.DetailedReviewAdapter
import com.mju.csmoa.home.review.adapter.PagingDataCommentAdapter
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.home.review.paging.CommentPagingDataSource.Companion.PARENT_COMMENT
import com.mju.csmoa.home.review.paging.PagingCommentViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToastStyle

class DetailedReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedReviewBinding
    private val pagingCommentViewModel: PagingCommentViewModel by viewModels()
    private lateinit var childCommentLauncher: ActivityResultLauncher<Intent>
    private lateinit var pagingDataCommentAdapter: PagingDataCommentAdapter
    private lateinit var detailedReviewAdapter: DetailedReviewAdapter

    private var detailedReview: DetailedReview? = null
    private var reviewId: Long? = null
    private var type: Int? = null
    private var position: Int? = null
    private var rootPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbarDetailedReviewToolbar)
        binding.toolbarDetailedReviewToolbar.setNavigationOnClickListener { onBackPressed() }

        // init launch (?????? ????????? ???????????? ???(ChildCommentActivity?????????) ???????????? ????????? ?????? ??????, ?????? ??? ???????????? ?????? ?????????)
        childCommentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val updatedParentComment =
                        result.data!!.getParcelableExtra<Comment>("parentComment")
                    val position = result.data!!.getIntExtra("position", -1)
                    if (updatedParentComment == null || position == -1) {
                        return@registerForActivityResult
                    }
                    // ?????? ?????? ??? ??? ??????
                    val originParentComment = pagingDataCommentAdapter.peek(position - 1)
                    originParentComment?.nestedCommentNum = updatedParentComment.nestedCommentNum
                    pagingDataCommentAdapter.notifyItemChanged(position - 1)
                }
            }

        if (!intent.hasExtra("reviewId") && !intent.hasExtra("position") && !intent.hasExtra("type")
        ) {
            Log.d(TAG, "?????? ??? ??? ??? ?????? ??????")
            MyApplication.makeToast(
                this,
                "?????? ?????? ??????",
                "?????? ???????????? ??????????????? ??????????????????.",
                MotionToastStyle.ERROR
            )
            return
        }

        // ????????? ????????????
        reviewId = intent.getLongExtra("reviewId", -1)
        position = intent.getIntExtra("position", -1)
        type = intent.getIntExtra("type", -1)

        if (reviewId == (-1).toLong() && position == -1 && type == -1) {
            MyApplication.makeToast(
                this,
                "?????? ?????? ??????",
                "?????? ???????????? ??????????????? ??????????????????.",
                MotionToastStyle.ERROR
            )
            return
        }
        if (type == 0) { // bestReview??? ???
            rootPosition = intent.getIntExtra("rootPosition", -1)
        }

        initRecyclerView()
        initInputParentComment()
    }

    // NOTE: ?????? ?????????????????? ??????
    private fun initRecyclerView() {
        lifecycleScope.launch(Dispatchers.IO) {
            val accessToken =
                MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
            val response = RetrofitManager.retrofitService?.getDetailedReview(
                accessToken ?: "",
                reviewId!!
            )
            if (response?.result == null) {
                launch(Dispatchers.Main) {
                    MyApplication.makeToast(
                        this@DetailedReviewActivity,
                        "?????? ?????? ??????",
                        "?????? ???????????? ??????????????? ??????????????????.",
                        MotionToastStyle.ERROR
                    )
                }
                return@launch
            }

            // NOTE: ????????? ???????????? / ????????? <-> ?????????
            val onLikeClicked: () -> Unit = {
                launch {
                    val postLikeResponse = RetrofitManager.retrofitService.postReviewLike(
                        accessToken = accessToken!!,
                        reviewId = detailedReview!!.reviewId
                    )

                    // ????????????
                    if (postLikeResponse.isSuccess && postLikeResponse.result != null) {
                        withContext(Dispatchers.Main) {
                            detailedReview?.isLike = !(detailedReview?.isLike)!!
                            detailedReview?.likeNum = if (detailedReview?.isLike!!)
                                detailedReview?.likeNum?.plus(1)!!
                            else
                                detailedReview?.likeNum?.minus(1)!!

                            detailedReviewAdapter.notifyItemChanged(0)
                        }
                    }
                }
            }

            // NOTE: ????????? ?????? ?????? ????????????
            val onChildCommentClicked: (position: Int) -> Unit = { position: Int ->
                val parentComment = pagingDataCommentAdapter.peek(position - 1)
                val childCommentIntent = Intent(
                    this@DetailedReviewActivity,
                    ChildCommentActivity::class.java
                ).apply {
                    putExtra("parentComment", parentComment)
                    putExtra("position", position)
                }
                childCommentLauncher.launch(childCommentIntent)
            }

            // NOTE: ????????? ?????? ?????? ????????? ??????
            val goToMapClicked = { anchorView: View, csBrand: String ->
                createBalloon(this@DetailedReviewActivity) {
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
                            this@DetailedReviewActivity,
                            R.drawable.ic_all_place
                        )
                    )
                    setBackgroundColorResource(R.color.balloon_color)
                    setOnBalloonClickListener {
                        // Map?????? ???????????? ?????? ???????????? ????????? ?????? ?????? ????????? ?????? ??????
                        EitherAOrBDialog(
                            context = this@DetailedReviewActivity,
                            theme = R.style.BottomSheetDialogTheme,
                            lottieName = "map.json",
                            title = "??????!!!",
                            message = "?????? ??????????????? ?????? ????????? ?????? ?????? ????????? :(",
                            buttonAText = "??????",
                            buttonBText = "??????",
                            onButtonAClicked = { },
                            ouButtonBClicked = {  // Map?????? ??????
                                this@DetailedReviewActivity.startActivity(
                                    Intent(
                                        this@DetailedReviewActivity,
                                        CSMapActivity::class.java
                                    ).apply {
                                        putExtra("csBrand", csBrand) // ????????? ????????? ?????? ??????
                                    })
                            }
                        ).show()
                    }
                    setBalloonAnimation(BalloonAnimation.FADE)
                    setLifecycleOwner(lifecycleOwner)
                }.showAlignBottom(anchorView)
            }

            // NOTE: ?????? ?????? ?????? ????????????
            detailedReview = response.result!!

            // NOTE: ?????????????????? ?????????
            detailedReviewAdapter =
                DetailedReviewAdapter(detailedReview!!, onLikeClicked, goToMapClicked)
            pagingDataCommentAdapter =
                PagingDataCommentAdapter(onChildCommentClicked)
            val concatAdapter =
                ConcatAdapter(detailedReviewAdapter, pagingDataCommentAdapter)

            launch(Dispatchers.Main) {
                binding.progressBarDetailedReviewLoading.visibility = View.INVISIBLE
                binding.recyclerViewDetailedReviewReviewAndComments.apply {
                    adapter = concatAdapter
                    addItemDecoration(RecyclerViewDecoration(15, 30, 20, 20))
                    layoutManager = LinearLayoutManager(
                        this@DetailedReviewActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                }
            }

            // NOTE: init viewModel
            pagingCommentViewModel.apply {
                setCommentType(
                    depth = PARENT_COMMENT,
                    id = response.result?.reviewId!!
                )
            }.getComments()
                .collectLatest { pagingData ->
                    pagingDataCommentAdapter.submitData(pagingData)
                }
        }
    }

    // NOTE: ?????? ??????
    private fun initInputParentComment() {
        binding.imageViewDetailedReviewSend.setOnClickListener {
            val content = binding.editTextDetailedReviewInputComment.text.toString().trim()
            if (content.isEmpty()) {
                MyApplication.makeToast(
                    this,
                    "????????? ??????",
                    "???????????? ??????????????????.",
                    MotionToastStyle.WARNING
                )
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBarDetailedReviewLoading.visibility = View.VISIBLE

                val deferredBaseResponse = async(Dispatchers.IO) {
                    val accessToken =
                        MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken!!
                    val requestBody: RequestBody =
                        content.toRequestBody("text/plain".toMediaTypeOrNull())

                    RetrofitManager.retrofitService?.postReviewParentComment(
                        reviewId = reviewId!!,
                        accessToken = accessToken,
                        content = requestBody
                    )
                }

                val response = deferredBaseResponse.await()
                binding.progressBarDetailedReviewLoading.visibility = View.INVISIBLE

                if (response?.isSuccess!! && response.result != null) { // ????????????
                    detailedReview!!.commentNum = detailedReview!!.commentNum.plus(1)
                    detailedReviewAdapter.notifyItemChanged(0) // header??? ?????? ?????? ????????? ?????? ???????????????
                    pagingDataCommentAdapter.refresh()
                    MyApplication.makeToast(
                        this@DetailedReviewActivity,
                        "?????? ??????",
                        "????????? ??????????????? ?????????????????????.",
                        MotionToastStyle.SUCCESS
                    )
                    binding.editTextDetailedReviewInputComment.setText("")
                } else {
                    MyApplication.makeToast(
                        this@DetailedReviewActivity,
                        "?????? ??????",
                        "????????? ???????????? ??? ??????????????????.",
                        MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (detailedReview != null && position != null && type != null) {
            val detailedReviewIntent = Intent().apply {
                putExtra("detailedReview", detailedReview)
                putExtra("position", position)
                putExtra("type", this@DetailedReviewActivity.type)
                if (rootPosition != null) {
                    putExtra("rootPosition", rootPosition)
                }
            }
            setResult(RESULT_OK, detailedReviewIntent)
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
}