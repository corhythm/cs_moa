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
import com.mju.csmoa.util.RecyclerViewDecoration


interface FilterItemClickListener {
    fun setOnFilterClicked(selectedFilterName: String, isClicked: Boolean)
}

class FilteringBottomSheetDialog(context: Context) : BottomSheetDialog(context),
    FilterItemClickListener {

    private lateinit var binding: DialogFileringBottomSheetBinding
    private val TAG = "로그"
    private var filteringCount = 0 // 필터링 개수

    private val csBrandMap = hashMapOf<String, Boolean>()
    private val eventTypeMap = hashMapOf<String, Boolean>()
    private val itemCategoryMap = hashMapOf<String, Boolean>()

    private val itemCsBrandRecyclerAdapter = ItemCsBrandRecyclerAdapter()
    private val itemEventTypeRecyclerAdapter = ItemEventTypeRecyclerAdapter()
    private val itemCategoryRecyclerAdapter = ItemCategoryRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFileringBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.textViewDialogFilteringCancel.setOnClickListener { dismiss() }

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
        val itemCsBrandList = ArrayList<ItemCsBrand>()

        Log.d(TAG, "toHashSet - ${itemCsBrandList.toHashSet()}")

        for (i in itemCsBrandNameList.indices) {
            itemCsBrandList.add(
                ItemCsBrand(
                    itemCsBrandNameList[i],
                    itemCsBrandLogoList.getResourceId(i, -1),
                    itemCsColorList[i]
                )
            )

            // add hashmap
            csBrandMap[itemCsBrandNameList[i]] = false
        }

        // init recyclerView
        itemCsBrandRecyclerAdapter.submitList(itemCsBrandList)
        itemCsBrandRecyclerAdapter.setFilterListener(this)
        binding.recyclerViewDialogFilteringCsBrandList.apply {
            adapter = itemCsBrandRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 20))
        }


        // 초기화 버튼 눌렸을 때
        binding.buttonDialogFilteringReset.setOnClickListener {

            csBrandMap.forEach { (k, _) -> csBrandMap[k] = false }
            eventTypeMap.forEach { (k, _)  ->  eventTypeMap[k] = false }
            itemCategoryMap.forEach { (k, _) -> itemCategoryMap[k] = false }

            itemCsBrandRecyclerAdapter.reset()
            itemEventTypeRecyclerAdapter.reset()
            itemCategoryRecyclerAdapter.reset()
            filteringCount = 0
            setFilteringButton()
        }
    }

    private fun initEventType() {

        // 이벤트 타입 리소스 가져오기
        val itemEventTypeColorList =
            context.resources.getStringArray(R.array.event_type_color_list)
        val itemEventTypeNameList =
            context.resources.getStringArray(R.array.event_type_list)
        val itemEventTypeList = ArrayList<ItemEventType>()

        for (i in itemEventTypeNameList.indices) {
            itemEventTypeList.add(
                ItemEventType(
                    eventType = itemEventTypeNameList[i],
                    textAndStrokeColor = itemEventTypeColorList[i]
                )
            )

            eventTypeMap[itemEventTypeColorList[i]] = false
        }

        // init recyclerView
        itemEventTypeRecyclerAdapter.submitList(itemEventTypeList)
        itemEventTypeRecyclerAdapter.setFilterListener(this)
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
        val itemCategoryList = ArrayList<ItemCategory>()

        for (i in itemCategoryNameList.indices) {
            itemCategoryList.add(
                ItemCategory(
                    backgroundResourceId = itemCategoryImageList.getResourceId(i, -1),
                    itemCategoryNameList[i]
                )
            )
            itemCategoryMap[itemCategoryNameList[i]] = false
        }

        // init recyclerView
        itemCategoryRecyclerAdapter.submitList(itemCategoryList)
        itemCategoryRecyclerAdapter.setFilterListener(this)
        binding.recyclerViewDialogFilteringItemCategoryList.apply {
            adapter = itemCategoryRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 100))
        }
    }

    override fun setOnFilterClicked(selectedFilterName: String, isClicked: Boolean) {

        if (csBrandMap.containsKey(selectedFilterName)) {
            csBrandMap[selectedFilterName] = isClicked
        }

        if (eventTypeMap.containsKey(selectedFilterName)) {
            eventTypeMap[selectedFilterName] = isClicked
        }

        if (itemCategoryMap.containsKey(selectedFilterName)) {
            itemCategoryMap[selectedFilterName] = isClicked
        }

        filteringCount = (if (isClicked) (filteringCount + 1) else (filteringCount - 1))
        setFilteringButton()
    }

    private fun setFilteringButton() {
        if (filteringCount > 0) {
            binding.buttonDialogFilteringReset.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#80B3E6"))
        } else {
            binding.buttonDialogFilteringReset.backgroundTintList =
                ColorStateList.valueOf(Color.GRAY)
        }
        binding.buttonDialogFilteringReset.text = "초기화($filteringCount)"
    }

}


