package com.mju.csmoa.home.event_item.viewholder.nested_viewholder

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventItemListBinding
import com.mju.csmoa.home.event_item.adpater.EventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.filter.FilteringBottomSheetDialog
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem

// 행사상품 리스트 화면
class EventItemListViewHolder(
    private val context: Context,
    private val itemEventItemListBinding: ItemEventItemListBinding
) :
    RecyclerView.ViewHolder(itemEventItemListBinding.root) {

//    private lateinit var eventItemList: List<ItemEventItem>

    fun setEventItemList(eventItemList: List<ItemEventItem>) {
//        this.eventItemList = eventItemList

        val eventItemRecyclerAdapter = EventItemRecyclerAdapter(context, eventItemList)
        itemEventItemListBinding.recyclerViewItemEventListEventItemList.apply {
            adapter = eventItemRecyclerAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        }

        itemEventItemListBinding.cardViewItemEventListFilter.setOnClickListener {
            FilteringBottomSheetDialog(context).show()
        }
    }


}