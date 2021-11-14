package com.mju.csmoa.home.event_item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mju.csmoa.R
import com.mju.csmoa.databinding.FragmentEventItemsBinding
import com.mju.csmoa.home.event_item.adpater.EventItemLoadStateAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.BODY
import com.mju.csmoa.home.event_item.adpater.EventItemPagingDataAdapter.Companion.HEADER
import com.mju.csmoa.home.event_item.adpater.SealedRecommendedEventItemAdapter
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.home.event_item.paging.EventItemViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.MyApplication
import com.mju.csmoa.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EventItemsFragment : Fragment() {

    private var _binding: FragmentEventItemsBinding? = null
    private val binding get() = _binding!!
    private val pagingDataAdapter = EventItemPagingDataAdapter()

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

        // 맨 위로 클릭했을 때
        binding.cardViewItemRecommendedEventGotoTop.setOnClickListener {
            binding.recyclerViewEventItemsRecommendationEventItems.scrollToPosition(0)
        }

        // 필터 버튼 클릭했을 때
        binding.cardViewItemRecommendedEventEventTypeContainer.setOnClickListener {
            FilteringBottomSheetDialog(requireContext()).show()
        }

    }

    private suspend fun initViewModel() {
        val viewModel = ViewModelProvider(this).get(EventItemViewModel::class.java)
        viewModel.getEventItems().collectLatest {
            pagingDataAdapter.submitData(it)
        }
    }

    private fun initRecyclerView() {
        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                // JwtToken은 발급 받을 때부터 만료되면 refresh 돼서 오기 때문에 null일 걱정은 없음
                val jwtToken = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()
                val response =
                    RetrofitManager.retrofitService?.getRecommendedEventItems(jwtToken!!.accessToken!!)
                val colorList = requireContext().resources.getStringArray(R.array.color_top10)

                response?.result?.forEachIndexed { index, itemEventItem ->
                    itemEventItem.colorCode = colorList[index]
                }

                val nestedRecommendedEventItemAdapter =
                    SealedRecommendedEventItemAdapter(response?.result!!)
                pagingDataAdapter.withLoadStateFooter(footer = EventItemLoadStateAdapter { pagingDataAdapter.refresh() })
                val concatAdapter =
                    ConcatAdapter(nestedRecommendedEventItemAdapter, pagingDataAdapter)

                launch(Dispatchers.Main) {
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

                // 아래 뷰모델 코드는 다른 위치에 두면 안 됨 (스레드 문제인 듯. 나중에 공부하자)
                initViewModel()

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
}