package com.mju.csmoa.home.event_item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.home.event_item.adpater.EventItemLoadStateAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.adpater.RecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.adpater.SealedRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.home.event_item.paging.PagingEventItemViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EventItemsFragment : Fragment(), EventItemChangedListener {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!
    private val pagingEventItemViewModel: PagingEventItemViewModel by activityViewModels()
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var pagingDataAdapter: EventItemPagingDataAdapter
    private lateinit var detailEventItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var recommendEventItems: List<EventItem>
    private var recommendedEventItemAdapter: RecommendedEventItemAdapter? = null // 이건 header 아이템 변경 시, notify용

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventItemsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        initRecyclerView()

        // init launcher
        detailEventItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null && result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val detailEventItem =
                        result.data!!.getParcelableExtra<EventItem>("detailEventItem")
                    val type = result.data!!.getIntExtra("type", -1)
                    val position = result.data!!.getIntExtra("position", -1)

                    Log.d(TAG, "in contracts / position = $position")
                    Log.d(TAG, "in contracts / type = $type")

                    val tempEventItem = if (type == HEADER) recommendEventItems[position]
                    else pagingDataAdapter.peek(position - 1)


                    tempEventItem!!.viewCount = detailEventItem!!.viewCount
                    tempEventItem.isLike = detailEventItem.isLike
                    tempEventItem.likeCount = detailEventItem.likeCount

                    if (type == HEADER) { // 이렇게 하는 게 맞는지 모르겠다. 너무 스파게티인 듯 ㅋㅋ
                        recommendedEventItemAdapter?.notifyItemChanged(position)
                    } else { // BODY
                        pagingDataAdapter.notifyItemChanged(position - 1)
                    }
                }
            }

        // 맨 위로 클릭했을 때
        binding.cardViewItemRecommendedEventGotoTop.setOnClickListener {
            binding.recyclerViewEventItemsRecommendationEventItems.scrollToPosition(0)
        }

        // 필터 버튼 클릭했을 때
        binding.cardViewItemRecommendedEventEventTypeContainer.setOnClickListener {
            FilteringBottomSheetDialog(requireContext()).show()
        }
    }

    private fun initRecyclerView() {
        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val colorList = requireContext().resources.getStringArray(R.array.color_top10)
                val jwtToken = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()
                val response =
                    RetrofitManager.retrofitService?.getRecommendedEventItems(jwtToken!!.accessToken!!)

                response?.result?.forEachIndexed { index, itemEventItem ->
                    itemEventItem.colorCode = colorList[index]
                }

                recommendEventItems = response?.result!! // 추천 행사 상품 리스트
                val sealedRecommendedEventItemAdapter =
                    SealedRecommendedEventItemAdapter(recommendEventItems, this@EventItemsFragment)
                pagingDataAdapter = EventItemPagingDataAdapter(this@EventItemsFragment)
                pagingDataAdapter.withLoadStateFooter(footer = EventItemLoadStateAdapter { pagingDataAdapter.retry() })
                concatAdapter =
                    ConcatAdapter(sealedRecommendedEventItemAdapter, pagingDataAdapter)


                withContext(Dispatchers.Main) {
                    binding.recyclerViewEventItemsRecommendationEventItems.apply {
                        addItemDecoration(RecyclerViewDecoration(0, 30, 10, 10))
                        adapter = concatAdapter
                        setHasFixedSize(true)
                        layoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.VERTICAL,
                            false
                        ).apply {
                            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    return when (concatAdapter.getItemViewType(position)) {
                                        HEADER -> 2
                                        BODY -> 1
                                        else -> -1
                                    }
                                }
                            }
                        }
                    }
                }

                // viewModel에서 데이터 감지
                pagingEventItemViewModel.getEventItems()
                    .collectLatest { pagingData -> pagingDataAdapter.submitData(pagingData) }

            }
        } catch (ex: Exception) {
            Log.d(TAG, "EventItemsFragment - exception / ${ex.printStackTrace()}")
            makeToast("데이터 가져오기 실패", "행사 상품 데이터를 가져오는 데 실패했습니다", MotionToastStyle.ERROR)
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
        _binding = null
        super.onDestroyView()
    }

    // 아이템 클릭 시
    override fun onClickedEventItem(type: Int, position: Int) {

        try {
            Log.d(TAG, "in onClickedEventItem / position = $position")
            Log.d(TAG, "in onClickedEventItem / type = $type")
            val eventItem = if (type == HEADER) recommendEventItems[position] // 추천 행사 상품
            else pagingDataAdapter.peek(position - 1) // 일반 행사 상품

            if (eventItem == null) {
                makeToast("상품 상세보기", "해당 행사 상품을 불러올 수 없습니다", MotionToastStyle.ERROR)
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                val accessToken =
                    MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                val response = RetrofitManager.retrofitService?.postEventItemHistory(
                    accessToken!!,
                    PostEventItemHistoryAndLikeReq(eventItem.eventItemId!!)
                )

                Log.d(TAG, "EventItemsFragment -onClickedEventItem() called / response = $response")

                launch(Dispatchers.Main) {
                    val detailEventItemIntent =
                        Intent(requireContext(), DetailEventItemActivity::class.java).apply {
                            putExtra("eventItemId", eventItem.eventItemId)
                            putExtra("type", type)
                            putExtra("position", position)
                        }
                    detailEventItemLauncher.launch(detailEventItemIntent)
                }
            }
        } catch (ex: Exception){
            Log.d(TAG, "EventItemsFragment -onClickedEventItem() called / exception = ${ex.printStackTrace()}")
            makeToast("행사 상품 상세 화면", "행사 상품 상세화면을 불러올 수 없습니다", MotionToastStyle.ERROR)
        }
    }


    override fun setRecommendedEventItemAdapter(recommendedEventItemAdapter: RecommendedEventItemAdapter) {
        this.recommendedEventItemAdapter = recommendedEventItemAdapter
    }
}

interface EventItemChangedListener {
    fun onClickedEventItem(type: Int, position: Int)
    fun setRecommendedEventItemAdapter(recommendedEventItemAdapter: RecommendedEventItemAdapter)
}