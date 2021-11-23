package com.mju.csmoa.home.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentReviewsBinding
import com.mju.csmoa.home.event_item.adapter.EventItemLoadStateAdapter
import com.mju.csmoa.home.review.adapter.PagingDataReviewAdapter
import com.mju.csmoa.home.review.adapter.SealedBestReviewsAdapter
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.paging.PagingReviewViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var sealedBestReviewsAdapter: SealedBestReviewsAdapter
    private lateinit var pagingDataAdapter: PagingDataReviewAdapter
    private val pagingReviewViewModel: PagingReviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        with(binding) {
            swipeLayoutReviewsRoot.setOnRefreshListener {
                initReviews()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    binding.swipeLayoutReviewsRoot.isRefreshing = false
                }
            }

            cardViewReviewsGotoTop.setOnClickListener {
                recyclerViewReviewsContainerReviews.scrollToPosition(0)
            }

            // 새 리뷰 작성
            cardViewReviewsWriteReview.setOnClickListener {
                startActivity(Intent(requireContext(), WriteReviewActivity::class.java))
            }
        }

        initReviews()
    }

    private fun initReviews() {
        // 데이터 가져오기

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val accessToken =
                    MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                val response =
                    RetrofitManager.retrofitService?.getBestReviews(accessToken = accessToken!!)

                withContext(Dispatchers.Main) {
                    binding.progressBarReviewsOnGoing.visibility = View.INVISIBLE

                    val bestReviews = mutableListOf<List<Review>>() //
                    val tempList = mutableListOf<Review>()
                    Log.d(TAG, "response?.result?.size = ${response?.result?.size}")
                    response?.result?.forEachIndexed { index, review ->
                        tempList.add(review) // 012 345 678 91011
                        if (index % 3 == 2) { // 2 5 8 11
                            bestReviews.add(tempList.toMutableList())
                            tempList.clear()
                        }
                    }

                    val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit = { position, rootPosition ->
                        Log.d(TAG, "ReviewsFragment -initReviews() called / $position, $rootPosition")
                    }

                    val reviewOnClicked: (position: Int) -> Unit = {

                    }

                    sealedBestReviewsAdapter = SealedBestReviewsAdapter(bestReviews, bestReviewOnClicked)
                    pagingDataAdapter = PagingDataReviewAdapter(reviewOnClicked)
                    concatAdapter = ConcatAdapter(sealedBestReviewsAdapter, pagingDataAdapter)

                    binding.recyclerViewReviewsContainerReviews.apply {
                        adapter = concatAdapter
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }

                pagingReviewViewModel.getReviews()
                    .collectLatest { pagingData -> pagingDataAdapter.submitData(pagingData) }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "ReviewsFragment - exception / ${ex.stackTrace}")
                    Log.d(TAG, "ReviewsFragment - exception / ${ex.message}")
                    makeToast("데이터 가져오기 실패", "행사 상품 데이터를 가져오는 데 실패했습니다", MotionToastStyle.ERROR)
                }
            }
        }


    }

    private fun makeToast(title: String, content: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            requireActivity(),
            title,
            content,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}