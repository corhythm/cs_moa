package com.mju.csmoa.home.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityDetailEventItemBinding
import com.mju.csmoa.databinding.ActivityDetailedReviewBinding
import com.mju.csmoa.home.review.adapter.DetailedReviewAdapter
import com.mju.csmoa.home.review.adapter.PagingDataCommentAdapter
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.paging.PagingCommentViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class DetailedReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedReviewBinding
    private val pagingCommentViewModel: PagingCommentViewModel by viewModels()

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

            if (intent.hasExtra("review")) { // review 객체가 존재하면
                val review = intent.getParcelableExtra<Review>("review")
                if (review == null) {
                    makeToast("리뷰 상세 정보", "리뷰 정보를 불러올 수 없습니다", MotionToastStyle.ERROR)
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
                    val onNestedCommentOnClicked: (position: Int) -> Unit = { }

                    if (response?.result != null) {
                        val detailedReviewAdapter = DetailedReviewAdapter(response.result!!)
                        val pagingDataCommentAdapter =
                            PagingDataCommentAdapter(onNestedCommentOnClicked)
                        val concatAdapter =
                            ConcatAdapter(detailedReviewAdapter, pagingDataCommentAdapter)

                        withContext(Dispatchers.Main) {
                            progressBarDetailedReviewLoading.visibility = View.INVISIBLE
                            recyclerViewDetailedReviewReviewAndComments.apply {
                                adapter = concatAdapter
                                layoutManager = LinearLayoutManager(
                                    this@DetailedReviewActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            }
                        }

                        pagingCommentViewModel.setReviewId(response.result?.reviewId!!)
                        pagingCommentViewModel.getComments()
                            .collectLatest { pagingData -> pagingDataCommentAdapter.submitData(pagingData) }
                    }

                } // lifeCycleOwner.launch
            } // if(intent.hasExtra)
        } // with(binding)


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