package com.mju.csmoa.home.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityChildCommentBinding
import com.mju.csmoa.home.review.adapter.PagingDataCommentAdapter
import com.mju.csmoa.home.review.adapter.ParentCommentHeaderAdapter
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.paging.CommentPagingDataSource
import com.mju.csmoa.home.review.paging.PagingCommentViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class ChildCommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChildCommentBinding
    private val commentViewModel: PagingCommentViewModel by viewModels()
    private var parentComment: Comment? = null
    private var position: Int? = null

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

            if (!intent.hasExtra("parentComment") && !intent.hasExtra("position")) {
                makeToast()
                return
            }

            parentComment = intent.getParcelableExtra("parentComment")
            position = intent.getIntExtra("position", -1)
            Log.d(TAG, "parentComment = $parentComment, position = $position")

            if (parentComment?.reviewCommentId == null || position == -1) {
                makeToast()
                return
            }

            val parentCommentHeaderAdapter = ParentCommentHeaderAdapter(parentComment!!)
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
                            id = parentComment!!.reviewCommentId
                        )
                    }.getComments().collectLatest { pagingData: PagingData<Comment> ->
                        Log.d(TAG, "pagingData = $pagingData")
                        pagingDataCommentAdapter.submitData(pagingData)
                    }
                }

                // 대댓글 입력
                imageViewChildCommentSend.setOnClickListener {
                    val content = editTextChildCommentInputComment.text.toString().trim()
                    if (content.isEmpty()) {
                        makeToast("대댓글 입력", "텍스트를 입력해주세요!", MotionToastStyle.WARNING)
                        return@setOnClickListener
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        progressBarChildCommentLoading.visibility = View.VISIBLE

                        val deferredBaseResponse = async(Dispatchers.IO) {
                            val accessToken =
                                MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken!!
                            val requestBody: RequestBody =
                                content.toRequestBody("text/plain".toMediaTypeOrNull())
                            RetrofitManager.retrofitService?.postReviewChildComment(
                                reviewId = parentComment!!.reviewId,
                                bundleId = parentComment!!.bundleId,
                                accessToken = accessToken,
                                content = requestBody
                            )
                        }

                        val response = deferredBaseResponse.await()
                        progressBarChildCommentLoading.visibility = View.INVISIBLE

                        if (response?.isSuccess!! && response.result != null) { // 성공하면
                            parentComment!!.nestedCommentNum = parentComment!!.nestedCommentNum.plus(1)
                            parentCommentHeaderAdapter.notifyItemChanged(0) // header에 있는 댓글 카운터 개수 증가시키고
                            pagingDataCommentAdapter.refresh()
                            makeToast("답글 입력", "답글이 성공적으로 입력되었습니다.", MotionToastStyle.SUCCESS)
                            editTextChildCommentInputComment.setText("")
                        } else {
                            makeToast("답글 입력", "답글을 등록하는 데 실패했습니다.", MotionToastStyle.ERROR)
                        }
                    }
                }

            } catch (ex: Exception) {
                Log.d(TAG, "NestedCommentActivity exception / ${ex.printStackTrace()}")
                makeToast()

            }

        }
    }

    override fun onBackPressed() {
        if (parentComment != null || position != null) {
            val childCommentIntent = Intent().apply {
                putExtra("parentComment", parentComment)
                putExtra("position", position)
            }
            setResult(RESULT_OK, childCommentIntent)
            super.onBackPressed()
        } else {
            super.onBackPressed()
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