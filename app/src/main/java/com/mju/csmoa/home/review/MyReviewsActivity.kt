package com.mju.csmoa.home.review

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.ActivityMyReviewsBinding
import com.mju.csmoa.home.review.adapter.PagingDataMyReviewAdapter
import com.mju.csmoa.home.review.paging.PagingMyReviewViewModel
import com.mju.csmoa.common.util.RecyclerViewDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReviewsBinding
    private val myReviewViewModel by viewModels<PagingMyReviewViewModel>()
    private lateinit var pagingMyReviewAdapter: PagingDataMyReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding) {
            setSupportActionBar(toolbarMyReviewsToolbar)
            toolbarMyReviewsToolbar.setNavigationOnClickListener { onBackPressed() }

            pagingMyReviewAdapter = PagingDataMyReviewAdapter { position: Int ->
                val detailedIntent =
                    Intent(this@MyReviewsActivity, DetailedReviewActivity::class.java)
                detailedIntent.apply {
                    val review = pagingMyReviewAdapter.peek(position)
                    putExtra("reviewId", review!!.reviewId)
                    putExtra("position", position) // 사실 이 값이랑
                    putExtra("type", 1) // 이 값은 필요 없음.
                }
                startActivity(detailedIntent)
            }
            recyclerViewMyReviewsMyReviews.apply {
                adapter = pagingMyReviewAdapter
                addItemDecoration(RecyclerViewDecoration(10, 10, 20, 20))
                layoutManager =
                    LinearLayoutManager(this@MyReviewsActivity, LinearLayoutManager.VERTICAL, false)
            }

            val whenLoadingFinished = { progressBarMyReviewsLoading.visibility = View.INVISIBLE }

            myReviewViewModel.setWhenLoadingFinished(whenLoadingFinished)
            lifecycleScope.launch(Dispatchers.IO) {
                myReviewViewModel.getMyReviews()
                    .collectLatest { pagingData -> pagingMyReviewAdapter.submitData(pagingData) }
            }
        }
    }
}