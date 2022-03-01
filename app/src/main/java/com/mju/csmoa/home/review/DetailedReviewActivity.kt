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

        // init launch (자식 댓글이 추가됐을 때(ChildCommentActivity로부터) 업데이트 사항이 있을 경우, 답글 수 업데이트 해야 하므로)
        childCommentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val updatedParentComment =
                        result.data!!.getParcelableExtra<Comment>("parentComment")
                    val position = result.data!!.getIntExtra("position", -1)
                    if (updatedParentComment == null || position == -1) {
                        return@registerForActivityResult
                    }
                    // 원래 댓글 내 값 변경
                    val originParentComment = pagingDataCommentAdapter.peek(position - 1)
                    originParentComment?.nestedCommentNum = updatedParentComment.nestedCommentNum
                    pagingDataCommentAdapter.notifyItemChanged(position - 1)
                }
            }

        if (!intent.hasExtra("reviewId") && !intent.hasExtra("position") && !intent.hasExtra("type")
        ) {
            Log.d(TAG, "필수 값 다 안 넘어 왔음")
            MyApplication.makeToast(
                this,
                "리뷰 상세 정보",
                "리뷰 데이터를 가져오는데 실패했습니다.",
                MotionToastStyle.ERROR
            )
            return
        }

        // 데이터 받아오기
        reviewId = intent.getLongExtra("reviewId", -1)
        position = intent.getIntExtra("position", -1)
        type = intent.getIntExtra("type", -1)

        if (reviewId == (-1).toLong() && position == -1 && type == -1) {
            MyApplication.makeToast(
                this,
                "리뷰 상세 정보",
                "리뷰 데이터를 가져오는데 실패했습니다.",
                MotionToastStyle.ERROR
            )
            return
        }
        if (type == 0) { // bestReview일 때
            rootPosition = intent.getIntExtra("rootPosition", -1)
        }

        initRecyclerView()
        initInputParentComment()
    }

    // NOTE: 전체 리사이클러뷰 설정
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
                        "리뷰 상세 정보",
                        "리뷰 데이터를 가져오는데 실패했습니다.",
                        MotionToastStyle.ERROR
                    )
                }
                return@launch
            }

            // NOTE: 좋아요 클릭하면 / 좋아요 <-> 싫어요
            val onLikeClicked: () -> Unit = {
                launch {
                    val postLikeResponse = RetrofitManager.retrofitService.postReviewLike(
                        accessToken = accessToken!!,
                        reviewId = detailedReview!!.reviewId
                    )

                    // 성공하면
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

            // NOTE: 댓글의 답글 보기 클릭하면
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

            // NOTE: 편의점 위치 보러 맵으로 이동
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
                    setText("가까운 주변 편의점 보러 가실래요?")
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
                        // Map으로 이동하기 전에 원하시는 물건이 없을 수도 있다고 사전 고지
                        EitherAOrBDialog(
                            context = this@DetailedReviewActivity,
                            theme = R.style.BottomSheetDialogTheme,
                            lottieName = "map.json",
                            title = "주의!!!",
                            message = "주변 편의점에는 해당 상품이 없을 수도 있어요 :(",
                            buttonAText = "취소",
                            buttonBText = "확인",
                            onButtonAClicked = { },
                            ouButtonBClicked = {  // Map으로 이동
                                this@DetailedReviewActivity.startActivity(
                                    Intent(
                                        this@DetailedReviewActivity,
                                        CSMapActivity::class.java
                                    ).apply {
                                        putExtra("csBrand", csBrand) // 편의점 브랜드 가치 전송
                                    })
                            }
                        ).show()
                    }
                    setBalloonAnimation(BalloonAnimation.FADE)
                    setLifecycleOwner(lifecycleOwner)
                }.showAlignBottom(anchorView)
            }

            // NOTE: 상세 리뷰 정보 가져오기
            detailedReview = response.result!!

            // NOTE: 리사이클러뷰 초기화
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

    // NOTE: 댓글 입력
    private fun initInputParentComment() {
        binding.imageViewDetailedReviewSend.setOnClickListener {
            val content = binding.editTextDetailedReviewInputComment.text.toString().trim()
            if (content.isEmpty()) {
                MyApplication.makeToast(
                    this,
                    "대댓글 입력",
                    "텍스트를 입력해주세요.",
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

                if (response?.isSuccess!! && response.result != null) { // 성공하면
                    detailedReview!!.commentNum = detailedReview!!.commentNum.plus(1)
                    detailedReviewAdapter.notifyItemChanged(0) // header에 있는 댓글 카운터 개수 증가시키고
                    pagingDataCommentAdapter.refresh()
                    MyApplication.makeToast(
                        this@DetailedReviewActivity,
                        "댓글 등록",
                        "댓글이 성공적으로 등록되었습니다.",
                        MotionToastStyle.SUCCESS
                    )
                    binding.editTextDetailedReviewInputComment.setText("")
                } else {
                    MyApplication.makeToast(
                        this@DetailedReviewActivity,
                        "댓글 등록",
                        "댓글을 등록하는 데 실패했습니다.",
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