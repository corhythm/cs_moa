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
import com.mju.csmoa.home.event_item.adapter.EventItemLoadStateAdapter
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.adapter.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.adapter.RecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.adapter.SealedRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.home.event_item.paging.PagingEventItemViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EventItemsFragment : Fragment(), EventItemChangedListener {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var detailEventItemLauncher: ActivityResultLauncher<Intent>

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var sealedRecommendedEventItemAdapter: SealedRecommendedEventItemAdapter
    private lateinit var pagingDataAdapter: EventItemPagingDataAdapter
    private lateinit var recommendEventItems: List<EventItem> // 추천 행사 상품 (10개)
    private var recommendedEventItemAdapter: RecommendedEventItemAdapter? = null // notify 용도

    private var csBrandMap = LinkedHashMap<String, Boolean>() // (cu, gs25, seven-eleven, ministop, emart24)
    private var eventTypeMap = LinkedHashMap<String, Boolean>() // 행사 종류 (1+1, 2+1, 3+1, 4+1)
    private var itemCategoryMap = LinkedHashMap<String, Boolean>() // (음료, 과자, 식품, 아이스크림)

    // query parameter로 넘겨줄 리스트
    private lateinit var csBrands: MutableList<String>
    private lateinit var eventTypes: MutableList<String>
    private lateinit var categories: MutableList<String>

    // pagingViewModel
    private val pagingEventItemViewModel: PagingEventItemViewModel by activityViewModels()

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

        // refresh할 때마다 추가가 되기 대문에 여기서 한 번만 설정
        binding.recyclerViewEventItemsRecommendationEventItems.addItemDecoration(
            RecyclerViewDecoration(0, 30, 10, 10)
        )

        // 맨 위로 클릭했을 때
        binding.cardViewEventItemsGotoTop.setOnClickListener {
            binding.recyclerViewEventItemsRecommendationEventItems.scrollToPosition(0)
        }

        csBrands =
            requireContext().resources.getStringArray(R.array.cs_brand_list).toMutableList() // 편의점 브랜드 이름
        eventTypes =
            requireContext().resources.getStringArray(R.array.event_type_list).toMutableList() // 행사 종류
        categories =
            requireContext().resources.getStringArray(R.array.item_category_list).toMutableList() // 상품 카테고리


        // filtering data init(처음에는 전부 true로 설정, 전부 false이면 아무 데이터도 못 받아옴)
        csBrands.forEach { csBrandName -> csBrandMap[csBrandName] = true }
        eventTypes.forEach { eventTypeName -> eventTypeMap[eventTypeName] = true }
        categories.forEach { categoryName -> itemCategoryMap[categoryName] = true }

        // 화면 다시 리프레시 (anonymous function)
        val viewRefresh: () -> Unit = {
            // 기존 데이터 삭제하고
            csBrands.clear()
            eventTypes.clear()
            categories.clear()
            // fitering 데이터 추가
            csBrandMap.forEach { (k, v) -> if (v) csBrands.add(k) }
            eventTypeMap.forEach { (k, v) -> if (v) eventTypes.add(k) }
            itemCategoryMap.forEach { (k, v) -> if (v) categories.add(k) }
            initRecyclerView()
            CoroutineScope(Dispatchers.Main).launch {
                delay(700)
                binding.swipeLayoutEventItemsRoot.isRefreshing = false
            }
        }

        // viewModel에 데이터 전달
        pagingEventItemViewModel.setFilterDataList(csBrands, eventTypes, categories)

        initRecyclerView()

        // 필터 버튼 클릭했을 때
        binding.cardViewEventItemsEventTypeContainer.setOnClickListener {
            FilteringBottomSheetDialog(
                requireContext(),
                csBrandMap = this.csBrandMap,
                eventTypeMap = this.eventTypeMap,
                itemCategoryMap = this.itemCategoryMap,
                whenDialogDestroyed = { // filtering 완료하면
                    viewRefresh.invoke()
                }
            ).show()
        }


        // refresh
        binding.swipeLayoutEventItemsRoot.setOnRefreshListener { viewRefresh.invoke() }

        // init launcher
        detailEventItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result != null && result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val detailEventItem =
                        result.data!!.getParcelableExtra<EventItem>("detailEventItem")
                    val type = result.data!!.getIntExtra("type", -1)
                    val position = result.data!!.getIntExtra("position", -1)

                    // 원래 행사 상품 객체 내 값 변경
                    val originEventItem = if (type == HEADER) recommendEventItems[position]
                    else pagingDataAdapter.peek(position - 1)

                    originEventItem!!.viewCount = detailEventItem!!.viewCount
                    originEventItem.isLike = detailEventItem.isLike
                    originEventItem.likeCount = detailEventItem.likeCount

                    if (type == HEADER) { // 이렇게 하는 게 맞는지 모르겠다. 너무 스파게티인 듯 ㅋㅋ
                        recommendedEventItemAdapter?.notifyItemChanged(position)
                    } else { // BODY
                        pagingDataAdapter.notifyItemChanged(position - 1)
                    }
                }
            }
    }

    private fun initRecyclerView() {
        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val colorList = requireContext().resources.getStringArray(R.array.color_top10)
                val jwtToken = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()

                val response =
                    RetrofitManager.retrofitService?.getRecommendedEventItems(
                        jwtToken!!.accessToken!!,
                        csBrands = this@EventItemsFragment.csBrands,
                        eventTypes = this@EventItemsFragment.eventTypes,
                        categories = this@EventItemsFragment.categories
                    )

                response?.result?.forEachIndexed { index, itemEventItem ->
                    itemEventItem.colorCode = colorList[index]
                }


                withContext(Dispatchers.Main) {
                    binding.progressBarEventItemsOnGoing.visibility = View.INVISIBLE
                    binding.recyclerViewEventItemsRecommendationEventItems.apply {
                        // 어댑터 선언을 여기서 안 하면 에러남
                        recommendEventItems = response?.result!! // 추천 행사 상품 리스트
                        sealedRecommendedEventItemAdapter =
                            SealedRecommendedEventItemAdapter(recommendEventItems, this@EventItemsFragment)
                        pagingDataAdapter = EventItemPagingDataAdapter(this@EventItemsFragment)
                        pagingDataAdapter.withLoadStateFooter(footer = EventItemLoadStateAdapter { pagingDataAdapter.retry() })
                        concatAdapter =
                            ConcatAdapter(sealedRecommendedEventItemAdapter, pagingDataAdapter)

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
        } catch (ex: Exception) {
            Log.d(
                TAG,
                "EventItemsFragment -onClickedEventItem() called / exception = ${ex.printStackTrace()}"
            )
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