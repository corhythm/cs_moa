package com.mju.csmoa.main.event_item.filter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mju.csmoa.R
import com.mju.csmoa.databinding.DialogFileringBottomSheetBinding
import com.mju.csmoa.util.RecyclerViewDecoration


interface FilterItemClickListener {
    fun setOnFilterClicked(whatFilter: String, position: Int)
}

class FilteringBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var binding: DialogFileringBottomSheetBinding
    private val TAG = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DialogFileringBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "FilteringBottomSheetDialog -onCreate() called")
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

        for (i in itemCsBrandNameList.indices) {
            itemCsBrandList.add(
                ItemCsBrand(
                    itemCsBrandNameList[i],
                    itemCsBrandLogoList.getResourceId(i, -1),
                    itemCsColorList[i]
                )
            )
            Log.d(TAG, "FilteringBottomSheetDialog -initCsBrand() called / ${itemCsBrandLogoList.getResourceId(i, -1)}")
        }

        // init recyclerView
        val itemCategoryRecyclerAdapter = ItemCsBrandRecyclerAdapter()
        itemCategoryRecyclerAdapter.submitList(itemCsBrandList)
        binding.recyclerViewDialogFilteringCsBrandList.apply {
            adapter = itemCategoryRecyclerAdapter
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
        val itemEventTypeList = ArrayList<ItemEventType>()

        for (i in itemEventTypeNameList.indices) {
            itemEventTypeList.add(
                ItemEventType(
                    eventType = itemEventTypeNameList[i],
                    textAndStrokeColor = itemEventTypeColorList[i]
                )
            )
        }

        // init recyclerView
        val itemEventTypeRecyclerAdapter = ItemEventTypeRecyclerAdapter()
        itemEventTypeRecyclerAdapter.submitList(itemEventTypeList)
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
        }

        // init recyclerView
        val itemCategoryRecyclerAdapter = ItemCategoryRecyclerAdapter()
        itemCategoryRecyclerAdapter.submitList(itemCategoryList)
        binding.recyclerViewDialogFilteringItemCategoryList.apply {
            adapter = itemCategoryRecyclerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RecyclerViewDecoration(top = 0, bottom = 0, start = 0, end = 100))
        }
    }

}


