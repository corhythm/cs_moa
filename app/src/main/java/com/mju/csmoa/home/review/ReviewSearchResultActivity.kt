package com.mju.csmoa.home.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.ActivityReviewSearchResultBinding
import com.mju.csmoa.home.NoSearchResultFragment
import com.mju.csmoa.home.review.adapter.PagingDataReviewSearchResultAdapter
import com.mju.csmoa.home.review.paging.PagingReviewViewModel
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import com.mju.csmoa.common.util.room.database.LocalRoomDatabase
import com.mju.csmoa.common.util.room.entity.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*

class ReviewSearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewSearchResultBinding
    private var searchWord: String? = null

    private lateinit var pagingDataReviewSearchResultAdapter: PagingDataReviewSearchResultAdapter
    private val pagingReviewViewModel by viewModels<PagingReviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 아무런 검색 결과 없으면
        val whenNoReviewSearchResult: () -> Unit = {
            binding.lottieAnimationViewReviewSearchResultLottie.visibility = View.INVISIBLE
            binding.textViewReviewSearchResultSearchWord.visibility = View.INVISIBLE
            binding.progressBarReviewSearchResultLoading.visibility = View.INVISIBLE
            binding.recyclerViewReviewSearchResultSearchResults.visibility = View.INVISIBLE

            supportFragmentManager.beginTransaction()
                .replace(
                    binding.frameLayoutReviewSearchResultContainer.id,
                    NoSearchResultFragment(searchWord)
                )
                .commit()
        }

        if (!intent.hasExtra("searchWord")) { // 검색어가 넘어오지 않았을 때 (근데 루프로는 진입할 일이 없기는 함)
            Log.d(TAG, "검색어 넘어오지 않음")
            whenNoReviewSearchResult()
            return
        }

        searchWord = intent.getStringExtra("searchWord")
        binding.textViewReviewSearchResultSearchWord.text = "'${searchWord}'에 대한 검색 결과입니다."
        saveSearchHistory(searchWord!!)


        try { // 데이터 가져오기
            val onReviewClicked: (position: Int) -> Unit = { position: Int ->
                // 이건 concat이랑 같이 쓸 때랑 달라서 position - 1하면 0번 선택하면 NPE 발생
                val review = pagingDataReviewSearchResultAdapter.peek(position)
                goToDetailedReview(
                    reviewId = review!!.reviewId,
                    position = position,
                    type = 1
                )
            }

            val whenSearchingComplete = {
                binding.progressBarReviewSearchResultLoading.visibility = View.INVISIBLE
            }

            pagingDataReviewSearchResultAdapter =
                PagingDataReviewSearchResultAdapter(onReviewClicked)

            binding.recyclerViewReviewSearchResultSearchResults.apply {
                adapter = pagingDataReviewSearchResultAdapter
                addItemDecoration(RecyclerViewDecoration(30, 30, 20, 20))
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(
                    this@ReviewSearchResultActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }

            // init viewModel
            lifecycleScope.launch(Dispatchers.IO) {
                pagingReviewViewModel.setSearchForInfo(searchWord!!, whenSearchingComplete, whenNoReviewSearchResult)
                pagingReviewViewModel.getReviews().collectLatest { pagingData ->
                    pagingDataReviewSearchResultAdapter.submitData(pagingData)
                }
            }

        } catch (ex: Exception) {
            Log.d(TAG, "ReviewsFragment - exception / ${ex.stackTrace}")
            Log.d(TAG, "ReviewsFragment - exception / ${ex.message}")

            MyApplication.makeToast(
                this@ReviewSearchResultActivity,
                "리뷰 검색 결과",
                "리뷰 검색 결과를 가져오는 데 실패했습니다.",
                MotionToastStyle.ERROR
            )
        }
    }

    // save search history
    private fun saveSearchHistory(searchWord: String) {
        if (searchWord.trim().isEmpty()) {
            return
        }
        val currentDate = SimpleDateFormat("yy.MM.dd HH:mm:ss", Locale.getDefault()).format(Date())
        val database = LocalRoomDatabase.getDatabase(this)

        lifecycleScope.launch {
            database.searchHistoryDao().insertSearchHistory(
                SearchHistory(searchWord = searchWord, createdAt = currentDate, type = 0)
            )
        }
    }

    private fun goToDetailedReview(
        reviewId: Long,
        position: Int,
        type: Int
    ) {
        val detailedReviewIntent =
            Intent(this, DetailedReviewActivity::class.java).apply {
                putExtra("reviewId", reviewId)
                putExtra("position", position)
                putExtra("type", type)
            }
        startActivity(detailedReviewIntent)
    }
}