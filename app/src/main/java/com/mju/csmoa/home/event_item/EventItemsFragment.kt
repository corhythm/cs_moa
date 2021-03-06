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
    private lateinit var recommendEventItems: List<EventItem> // ?????? ?????? ?????? (10???)
    private var recommendedEventItemAdapter: RecommendedEventItemAdapter? = null // notify ??????

    private var csBrandMap = LinkedHashMap<String, Boolean>() // (cu, gs25, seven-eleven, ministop, emart24)
    private var eventTypeMap = LinkedHashMap<String, Boolean>() // ?????? ?????? (1+1, 2+1, 3+1, 4+1)
    private var itemCategoryMap = LinkedHashMap<String, Boolean>() // (??????, ??????, ??????, ???????????????)

    // query parameter??? ????????? ?????????
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

        // refresh??? ????????? ????????? ?????? ????????? ????????? ??? ?????? ??????
        binding.recyclerViewEventItemsRecommendationEventItems.addItemDecoration(
            RecyclerViewDecoration(0, 30, 10, 10)
        )

        // ??? ?????? ???????????? ???
        binding.cardViewEventItemsGotoTop.setOnClickListener {
            binding.recyclerViewEventItemsRecommendationEventItems.scrollToPosition(0)
        }

        csBrands =
            requireContext().resources.getStringArray(R.array.cs_brand_list).toMutableList() // ????????? ????????? ??????
        eventTypes =
            requireContext().resources.getStringArray(R.array.event_type_list).toMutableList() // ?????? ??????
        categories =
            requireContext().resources.getStringArray(R.array.item_category_list).toMutableList() // ?????? ????????????


        // filtering data init(???????????? ?????? true??? ??????, ?????? false?????? ?????? ???????????? ??? ?????????)
        csBrands.forEach { csBrandName -> csBrandMap[csBrandName] = true }
        eventTypes.forEach { eventTypeName -> eventTypeMap[eventTypeName] = true }
        categories.forEach { categoryName -> itemCategoryMap[categoryName] = true }

        // ?????? ?????? ???????????? (anonymous function)
        val viewRefresh: () -> Unit = {
            // ?????? ????????? ????????????
            csBrands.clear()
            eventTypes.clear()
            categories.clear()
            // fitering ????????? ??????
            csBrandMap.forEach { (k, v) -> if (v) csBrands.add(k) }
            eventTypeMap.forEach { (k, v) -> if (v) eventTypes.add(k) }
            itemCategoryMap.forEach { (k, v) -> if (v) categories.add(k) }
            initRecyclerView()
            CoroutineScope(Dispatchers.Main).launch {
                delay(700)
                binding.swipeLayoutEventItemsRoot.isRefreshing = false
            }
        }

        // viewModel??? ????????? ??????
        pagingEventItemViewModel.setFilterDataList(csBrands, eventTypes, categories)

        initRecyclerView()

        // ?????? ?????? ???????????? ???
        binding.cardViewEventItemsEventTypeContainer.setOnClickListener {
            FilteringBottomSheetDialog(
                requireContext(),
                csBrandMap = this.csBrandMap,
                eventTypeMap = this.eventTypeMap,
                itemCategoryMap = this.itemCategoryMap,
                whenDialogDestroyed = { // filtering ????????????
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

                    // ?????? ?????? ?????? ?????? ??? ??? ??????
                    val originEventItem = if (type == HEADER) recommendEventItems[position]
                    else pagingDataAdapter.peek(position - 1)

                    originEventItem!!.viewCount = detailEventItem!!.viewCount
                    originEventItem.isLike = detailEventItem.isLike
                    originEventItem.likeCount = detailEventItem.likeCount

                    if (type == HEADER) { // ????????? ?????? ??? ????????? ????????????. ?????? ??????????????? ??? ??????
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
                        // ????????? ????????? ????????? ??? ?????? ?????????
                        recommendEventItems = response?.result!! // ?????? ?????? ?????? ?????????
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

                // viewModel?????? ????????? ??????
                pagingEventItemViewModel.getEventItems()
                    .collectLatest { pagingData -> pagingDataAdapter.submitData(pagingData) }

            }
        } catch (ex: Exception) {
            Log.d(TAG, "EventItemsFragment - exception / ${ex.printStackTrace()}")
            makeToast("????????? ???????????? ??????", "?????? ?????? ???????????? ???????????? ??? ??????????????????", MotionToastStyle.ERROR)
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

    // ????????? ?????? ???
    override fun onClickedEventItem(type: Int, position: Int) {

        try {
            Log.d(TAG, "in onClickedEventItem / position = $position")
            Log.d(TAG, "in onClickedEventItem / type = $type")
            val eventItem = if (type == HEADER) recommendEventItems[position] // ?????? ?????? ??????
            else pagingDataAdapter.peek(position - 1) // ?????? ?????? ??????

            if (eventItem == null) {
                makeToast("?????? ????????????", "?????? ?????? ????????? ????????? ??? ????????????", MotionToastStyle.ERROR)
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
            makeToast("?????? ?????? ?????? ??????", "?????? ?????? ??????????????? ????????? ??? ????????????", MotionToastStyle.ERROR)
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