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
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration

class FilteringBottomSheetDialog(
    context: Context,
    private val csBrandMap: LinkedHashMap<String, Boolean>,
    private val eventTypeMap: LinkedHashMap<String, Boolean>,
    private val itemCategoryMap: LinkedHashMap<String, Boolean>,
    private val whenDialogDestroyed: (
        csBrandMap: LinkedHashMap<String, Boolean>,
        eventTypeMap: LinkedHashMap<String, Boolean>,
        itemCategoryMap: LinkedHashMap<String, Boolean>
    ) -> Unit
) : BottomSheetDialog(context), FilterItemClickListener {

    private lateinit var binding: DialogFileringBottomSheetBinding
    private var filteringCount = 0 // 필터링 개수

    private lateinit var itemCsBrandRecyclerAdapter: ItemCsBrandRecyclerAdapter
    private lateinit var itemEventTypeRecyclerAdapter: ItemEventTypeRecyclerAdapter
    private lateinit var itemCategoryRecyclerAdapter: ItemCategoryRecyclerAdapter

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
        binding.textViewDialogFilteringCancel.setOnClickListener { dismiss() }
        binding.textViewDialogFilteringFilteringApply.setOnClickListener {
            dismiss()
            whenDialogDestroyed(this.csBrandMap, this.eventTypeMap, this.itemCategoryMap)
        }

        // 현재 이미 클릭된 개수 카운트
        csBrandMap.forEach { (_, v) -> if (v) filteringCount++ }
        eventTypeMap.forEach { (_, v) -> if (v) filteringCount++ }
        eventTypeMap.forEach { (_, v) -> if (v) filteringCount++ }
        setFilteringButton()


        // 초기화 버튼 눌렸을 때
        binding.buttonDialogFilteringReset.setOnClickListener {

            csBrandMap.forEach { (k, _) -> csBrandMap[k] = false }
            eventTypeMap.forEach { (k, _) -> eventTypeMap[k] = false }
            itemCategoryMap.forEach { (k, _) -> itemCategoryMap[k] = false }

            itemCsBrandList.forEach { itemCsBrand -> itemCsBrand.isClicked = false }
            itemEventTypeList.forEach { itemEventType -> itemEventType.isClicked = false }
            itemCategoryList.forEach { itemCategory -> itemCategory.isClicked = false }

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

        // 편의점 브랜드
        if (csBrandMap.containsKey(selectedFilterName)) {
            csBrandMap[selectedFilterName] = isClicked
            itemCsBrandRecyclerAdapter.notifyItemChanged(position)
        }

        // 행사 종류
        if (eventTypeMap.containsKey(selectedFilterName)) {
            eventTypeMap[selectedFilterName] = isClicked
            itemEventTypeRecyclerAdapter.notifyItemChanged(position)
        }

        // 카테고리
        if (itemCategoryMap.containsKey(selectedFilterName)) {
            itemCategoryMap[selectedFilterName] = isClicked
            itemCategoryRecyclerAdapter.notifyItemChanged(position)
        }

        if (isClicked) filteringCount++ else filteringCount--
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