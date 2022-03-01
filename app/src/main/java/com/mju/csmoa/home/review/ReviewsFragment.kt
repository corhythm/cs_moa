package com.mju.csmoa.home.review

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.FragmentReviewsBinding
import com.mju.csmoa.home.review.adapter.PagingDataReviewAdapter
import com.mju.csmoa.home.review.adapter.SealedBestReviewsAdapter
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.paging.PagingReviewViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToastStyle

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var sealedBestReviewsAdapter: SealedBestReviewsAdapter
    private lateinit var pagingDataReviewAdapter: PagingDataReviewAdapter
    private val pagingReviewViewModel: PagingReviewViewModel by activityViewModels()
    private lateinit var detailedReviewLauncher: ActivityResultLauncher<Intent>
    private lateinit var writeReviewLauncher: ActivityResultLauncher<Intent>
    private lateinit var bestReviews: List<List<Review>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        // 상세보기 후 다시 현 액티비티로 왔을 때 변한 값 업데이트
        detailedReviewLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val detailedReview =
                        result.data!!.getParcelableExtra<DetailedReview>("detailedReview")
                    val type = result.data!!.getIntExtra("type", -1)
                    val position = result.data!!.getIntExtra("position", -1)
                    val rootPosition: Int?
                    val review: Review?

                    if (detailedReview == null || type == -1 || position == -1) {
                        Log.d(TAG, "비정상 종료")
                        return@registerForActivityResult
                    }

                    if (type == 0) {
                        rootPosition = result.data!!.getIntExtra("rootPosition", -1)
                        review = bestReviews[position][rootPosition]
                    } else {
                        review = pagingDataReviewAdapter.peek(position - 1)
                    }

                    review?.viewNum = detailedReview.viewNum
                    review?.likeNum = detailedReview.likeNum
                    review?.isLike = detailedReview.isLike
                    review?.commentNum = detailedReview.commentNum

                    if (type == 0) { // 여기는 nested adapter까지 전달해줘야 함.
                        sealedBestReviewsAdapter.notifyItemRangeChanged(0, bestReviews.size)
                    } else { // 여기서도 에러남
                        pagingDataReviewAdapter.notifyItemChanged(position - 1)
                    }
                }
            }


        // 권한 허용 요구
        val requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean> ->
                permissions.forEach { (_, v) ->
                    if (!v) {
                        MyApplication.makeToast(
                            requireActivity(),
                            "권한 설정",
                            "권한에 모두 동의해주세요",
                            MotionToastStyle.WARNING
                        )
                        requireActivity().finish()
                    }
                }
            }

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )

        // 새로운 게시글 작성 후 다시 현재 액티비로 오면 작성한 게시글 맨 위로 업데이트
        writeReviewLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                Log.d(TAG, "writeReview 끝난 후 / result = $result / result.data = ${result.data}")
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    pagingDataReviewAdapter.refresh()
                }
            }


        with(binding) {
            swipeLayoutReviewsRoot.setOnRefreshListener {
                initReviews()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    binding.swipeLayoutReviewsRoot.isRefreshing = false
                }
            }
            // 스크롤 맨 위로 이동
            cardViewReviewsGotoTop.setOnClickListener {
                recyclerViewReviewsContainerReviews.scrollToPosition(0)
            }
            // 새 리뷰 작성
            cardViewReviewsWriteReview.setOnClickListener {
                val writeReviewIntent = Intent(requireContext(), WriteReviewActivity::class.java)
                writeReviewLauncher.launch(writeReviewIntent)
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

                    bestReviews = mutableListOf() //
                    val tempList = mutableListOf<Review>()
                    Log.d(TAG, "response?.result?.size = ${response?.result?.size}")
                    response?.result?.forEachIndexed { index, review ->
                        tempList.add(review) // 012 345 678 91011
                        if (index % 3 == 2) { // 2 5 8 11
                            (bestReviews as MutableList<List<Review>>).add(tempList.toMutableList())
                            tempList.clear()
                        }
                    }

                    val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit =
                        { position, rootPosition ->
                            goToDetailedReview(
                                reviewId = bestReviews[position][rootPosition].reviewId,
                                position = position,
                                rootPosition = rootPosition,
                                type = 0
                            )
                        }

                    val reviewOnClicked: (position: Int) -> Unit = { position ->
                        val review = pagingDataReviewAdapter.peek(position - 1)
                        goToDetailedReview(
                            reviewId = review!!.reviewId,
                            position = position,
                            type = 1
                        )
                    }

                    sealedBestReviewsAdapter =
                        SealedBestReviewsAdapter(bestReviews, bestReviewOnClicked)
                    pagingDataReviewAdapter = PagingDataReviewAdapter(reviewOnClicked)
                    concatAdapter = ConcatAdapter(sealedBestReviewsAdapter, pagingDataReviewAdapter)

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
                    .collectLatest { pagingData -> pagingDataReviewAdapter.submitData(pagingData) }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "ReviewsFragment - exception / ${ex.stackTrace}")
                    Log.d(TAG, "ReviewsFragment - exception / ${ex.message}")
                    MyApplication.makeToast(
                        requireActivity(),
                        "리뷰 보기",
                        "리뷰 데이터를 가져오는 데 실패했습니다.",
                        MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToDetailedReview(
        reviewId: Long,
        position: Int,
        rootPosition: Int? = null,
        type: Int
    ) {
        val detailedReviewIntent =
            Intent(requireContext(), DetailedReviewActivity::class.java).apply {
                putExtra("reviewId", reviewId)
                putExtra("position", position)
                putExtra("type", type)
                if (rootPosition != null) {
                    putExtra("rootPosition", rootPosition)
                }
            }
        detailedReviewLauncher.launch(detailedReviewIntent)
    }
}