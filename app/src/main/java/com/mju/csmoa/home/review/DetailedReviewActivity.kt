package com.mju.csmoa.home.review

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityDetailedReviewBinding
import com.mju.csmoa.home.review.adapter.DetailedReviewAdapter
import com.mju.csmoa.home.review.adapter.PagingDataCommentAdapter
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.paging.CommentPagingDataSource.Companion.PARENT_COMMENT
import com.mju.csmoa.home.review.paging.PagingCommentViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.MyApplication
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class DetailedReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedReviewBinding
    private val pagingCommentViewModel: PagingCommentViewModel by viewModels()
    private lateinit var childCommentLauncher: ActivityResultLauncher<Intent>
    private lateinit var pagingDataCommentAdapter: PagingDataCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding) {
            setSupportActionBar(toolbarDetailedReviewToolbar)
            toolbarDetailedReviewToolbar.setNavigationOnClickListener { onBackPressed() }

            // init launch
            childCommentLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {

                    }
                }

            if (intent.hasExtra("review")) { // review 객체가 존재하면
                val review = intent.getParcelableExtra<Review>("review")
                if (review == null) {
                    makeToast()
                    return
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    val accessToken =
                        MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                    val response = RetrofitManager.retrofitService?.getDetailedReview(
                        accessToken ?: "",
                        review.reviewId
                    )

                    // 답글 보기 클릭하면
                    val onNestedCommentOnClicked: (position: Int) -> Unit = { position: Int ->
                        val parentComment = pagingDataCommentAdapter.peek(position - 1)
                        val childCommentIntent = Intent(
                            this@DetailedReviewActivity,
                            ChildCommentActivity::class.java
                        ).apply {
                            putExtra("parentComment", parentComment)
                        }
                        childCommentLauncher.launch(childCommentIntent)
                    }

                    if (response?.result != null) {
                        val detailedReviewAdapter = DetailedReviewAdapter(response.result!!)
                        pagingDataCommentAdapter =
                            PagingDataCommentAdapter(onNestedCommentOnClicked)
                        val concatAdapter =
                            ConcatAdapter(detailedReviewAdapter, pagingDataCommentAdapter)

                        withContext(Dispatchers.Main) {
                            progressBarDetailedReviewLoading.visibility = View.INVISIBLE
                            recyclerViewDetailedReviewReviewAndComments.apply {
                                adapter = concatAdapter
                                addItemDecoration(RecyclerViewDecoration(15, 30, 20, 20))
                                layoutManager = LinearLayoutManager(
                                    this@DetailedReviewActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            }
                        }

                        pagingCommentViewModel.apply {
                            setCommentType(
                                depth = PARENT_COMMENT,
                                id = response.result?.reviewId!!
                            )
                        }.getComments()
                            .collectLatest { pagingData ->
                                pagingDataCommentAdapter.submitData(
                                    pagingData
                                )
                            }
                    }

                }
            }
        }


    }

    private fun makeToast(
        title: String = "리뷰 상세 정보",
        content: String = "리뷰 데이터를 가져오는데 실패했습니다",
        motionToastStyle: MotionToastStyle = MotionToastStyle.ERROR
    ) {
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