package com.mju.csmoa.home.event_item.filter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.R
import com.mju.csmoa.databinding.DialogFileringBottomSheetBinding
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.RecyclerViewDecoration

class FilteringBottomSheetDialog(
    context: Context,
    private var csBrandMap: LinkedHashMap<String, Boolean>,
    private var eventTypeMap: LinkedHashMap<String, Boolean>,
    private var itemCategoryMap: LinkedHashMap<String, Boolean>,
    private val whenDialogDestroyed: () -> Unit
) : BottomSheetDialog(context), FilterItemClickListener {

    private lateinit var binding: DialogFileringBottomSheetBinding
    private var filteringCount = 0 // 필터링 개수

    private lateinit var itemCsBrandRecyclerAdapter: ItemCsBrandRecyclerAdapter
    private lateinit var itemEventTypeRecyclerAdapter: ItemEventTypeRecyclerAdapter
    private lateinit var itemCategoryRecyclerAdapter: ItemCategoryRecyclerAdapter

    // 초기화를 눌렀을 때 원래 LinkedHashMap에 있는 값들 임시저장
    private lateinit var originCsBrandMap: LinkedHashMap<String, Boolean>
    private lateinit var originEventTypeMap: LinkedHashMap<String, Boolean>
    private lateinit var originItemCategoryMap: LinkedHashMap<String, Boolean>

    private val itemCsBrandList = ArrayList<ItemCsBrand>()
    private val itemEventTypeList = ArrayList<ItemEventType>()
    private val itemCategoryList = ArrayList<ItemCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFileringBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {


        // 현재 이미 클릭된 개수 카운트
        csBrandMap.forEach { (k, v) ->
            Log.d(TAG, "과연: k = $k, v = $v, csBrandMap[$k] = ${csBrandMap[k]} ")
            if (v) ++filteringCount
        }
        eventTypeMap.forEach { (_, v) -> if (v) ++filteringCount }
        itemCategoryMap.forEach { (_, v) -> if (v) ++filteringCount }
        setFilteringButton() // 필터링 버튼에 현재 클릭된 개수 반영

        // NOTE: 클릭만 하고 취소 누르면 원래 값을 유지해야 하므로 여기서 임시 값 저장
        originCsBrandMap = csBrandMap.toMutableMap() as LinkedHashMap<String, Boolean>
        originEventTypeMap = eventTypeMap.toMutableMap() as LinkedHashMap<String, Boolean>
        originItemCategoryMap = itemCategoryMap.toMutableMap() as LinkedHashMap<String, Boolean>

        // cancel
        binding.textViewDialogFilteringCancel.setOnClickListener {

            csBrandMap.forEach { (k, _) -> csBrandMap[k] = originCsBrandMap.getValue(k) }
            eventTypeMap.forEach { (k, _) -> eventTypeMap[k] = originEventTypeMap.getValue(k) }
            itemCategoryMap.forEach { (k, _) ->
                itemCategoryMap[k] = originItemCategoryMap.getValue(k)
            }
            dismiss()
        }

        // apply
        binding.textViewDialogFilteringFilteringApply.setOnClickListener {
            dismiss()
            whenDialogDestroyed()
        }

        // 초기화 버튼 눌렸을 때
        binding.buttonDialogFilteringReset.setOnClickListener {

            // data isClicked -> false
            itemCsBrandList.forEach { itemCsBrand -> itemCsBrand.isClicked = false }
            itemEventTypeList.forEach { itemEventType -> itemEventType.isClicked = false }
            itemCategoryList.forEach { itemCategory -> itemCategory.isClicked = false }

            // hashMap replaceAll -> false
            csBrandMap.forEach { (k, _) -> csBrandMap[k] = false }
            eventTypeMap.forEach { (k, _) -> eventTypeMap[k] = false }
            itemCategoryMap.forEach { (k, _) -> itemCategoryMap[k] = false }

            itemCsBrandRecyclerAdapter.notifyItemRangeChanged(0, itemCsBrandList.size)
            itemEventTypeRecyclerAdapter.notifyItemRangeChanged(0, itemEventTypeList.size)
            itemCategoryRecyclerAdapter.notifyItemRangeChanged(0, itemCategoryList.size)

            filteringCount = 0
            setFilteringButton()
        }

        initCsBrand()
        initEventType()
        initItemCategory()
    }

    private fun initCsBrand() {
        // 편의점 리소스 가져오기
        val itemCsBrandNameList =
            context.resources.getStringArray(R.array.cs_brand_list)
        val itemCsBrandLogoList =
            context.resources.obtainTypedArray(R.array.cs_brand_logo_list)
        val itemCsColorList =
            context.resources.getStringArray(R.array.cs_brand_color_list)


        for (i in itemCsBrandNameList.indices) {
            itemCsBrandList.add(
                ItemCsBrand(
                    brand = itemCsBrandNameList[i],
                    csImageResourceId = itemCsBrandLogoList.getResourceId(i, -1),
                    csColor = itemCsColorList[i],
                    isClicked = csBrandMap.getValue(itemCsBrandNameList[i])
                )
            )
        }

        // init CsBrand recyclerView
        itemCsBrandRecyclerAdapter = ItemCsBrandRecyclerAdapter(itemCsBrandList, this)
        binding.recyclerViewDialogFilteringCsBrandList.apply {
            adapter = itemCsBrandRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 20))
        }
    }

    private fun initEventType() {

        // 이벤트 타입 리소스 가져오기
        val itemEventTypeColorList =
            context.resources.getStringArray(R.array.event_type_color_list)
        val itemEventTypeNameList =
            context.resources.getStringArray(R.array.event_type_list)

        for (i in itemEventTypeNameList.indices) {
            itemEventTypeList.add(
                ItemEventType(
                    eventType = itemEventTypeNameList[i],
                    textAndStrokeColor = itemEventTypeColorList[i],
                    isClicked = eventTypeMap.getValue(itemEventTypeNameList[i])
                )
            )
        }

        // init recyclerView
        itemEventTypeRecyclerAdapter = ItemEventTypeRecyclerAdapter(itemEventTypeList, this)
        binding.recyclerViewDialogFilteringEventTypeList.apply {
            adapter = itemEventTypeRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 50))
        }
    }

    private fun initItemCategory() {
        // 카테고리 리소스 가져오기
        val itemCategoryNameList =
            context.resources.getStringArray(R.array.item_category_list)
        val itemCategoryImageList =
            context.resources.obtainTypedArray(R.array.item_category_image_list)

        for (i in itemCategoryNameList.indices) {
            itemCategoryList.add(
                ItemCategory(
                    backgroundResourceId = itemCategoryImageList.getResourceId(i, -1),
                    categoryType = itemCategoryNameList[i],
                    isClicked = itemCategoryMap.getValue(itemCategoryNameList[i])
                )
            )
        }

        // init recyclerView
        itemCategoryRecyclerAdapter = ItemCategoryRecyclerAdapter(itemCategoryList, this)
        binding.recyclerViewDialogFilteringItemCategoryList.apply {
            adapter = itemCategoryRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 100))
        }
    }

    // 필터링 아이템 클릭됐을 때
    override fun setOnFilterClicked(selectedFilterName: String, position: Int, isClicked: Boolean) {

        if (csBrandMap.containsKey(selectedFilterName)) { // 편의점 브랜드
            csBrandMap[selectedFilterName] = isClicked
            itemCsBrandRecyclerAdapter.notifyItemChanged(position)
        }

        if (eventTypeMap.containsKey(selectedFilterName)) { // 행사 종류
            eventTypeMap[selectedFilterName] = isClicked
            itemEventTypeRecyclerAdapter.notifyItemChanged(position)
        }

        if (itemCategoryMap.containsKey(selectedFilterName)) { // 카테고리
            itemCategoryMap[selectedFilterName] = isClicked
            itemCategoryRecyclerAdapter.notifyItemChanged(position)
        }

        if (isClicked) ++filteringCount else --filteringCount
        setFilteringButton()
    }

    private fun setFilteringButton() {
        if (filteringCount > 0) {
            binding.buttonDialogFilteringReset.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#008061"))
        } else {
            binding.buttonDialogFilteringReset.backgroundTintList =
                ColorStateList.valueOf(Color.GRAY)
        }
        binding.buttonDialogFilteringReset.text = "초기화($filteringCount)"
    }
}


interface FilterItemClickListener {
    fun setOnFilterClicked(selectedFilterName: String, position: Int, isClicked: Boolean)
}