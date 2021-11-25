package com.mju.csmoa.home.review

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityChildCommentBinding
import com.mju.csmoa.home.review.adapter.PagingDataCommentAdapter
import com.mju.csmoa.home.review.adapter.ParentCommentHeaderAdapter
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.paging.CommentPagingDataSource
import com.mju.csmoa.home.review.paging.PagingCommentViewModel
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ChildCommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChildCommentBinding
    private val commentViewModel: PagingCommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding) {
            setSupportActionBar(toolbarChildCommentToolbar)
            toolbarChildCommentToolbar.setNavigationOnClickListener { onBackPressed() }

            if (!intent.hasExtra("parentComment")) {
                makeToast()
                return
            }

            val parentComment = intent.getParcelableExtra<Comment>("parentComment")
            if (parentComment?.reviewCommentId == null) {
                makeToast()
                return
            }

            val parentCommentHeaderAdapter = ParentCommentHeaderAdapter(parentComment)
            val pagingDataCommentAdapter = PagingDataCommentAdapter()
            val concatAdapter = ConcatAdapter(parentCommentHeaderAdapter, pagingDataCommentAdapter)

            try {
                progressBarChildCommentLoading.visibility = View.INVISIBLE
                recyclerViewChildCommentReviewAndComments.apply {
                    adapter = concatAdapter
                    layoutManager = LinearLayoutManager(
                        this@ChildCommentActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    addItemDecoration(RecyclerViewDecoration(15, 30, 20, 20))
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    commentViewModel.apply {
                        setCommentType(
                            depth = CommentPagingDataSource.CHILD_COMMENT,
                            id = parentComment.reviewCommentId
                        )
                    }.getComments()
                        .collectLatest { pagingData ->
                            Log.d(TAG, "pagingData = $pagingData")
                            pagingDataCommentAdapter.submitData(
                                pagingData
                            )
                        }
                }

            } catch (ex: Exception) {
                Log.d(TAG, "NestedCommentActivity exception / ${ex.printStackTrace()}")
                makeToast()

            }

        }
    }

    private fun makeToast(
        title: String = "답글 보기",
        content: String = "답글을 가져오는 데 실패했습니다",
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