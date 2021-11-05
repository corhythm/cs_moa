package com.mju.csmoa.home.event_item.viewholder.nested_viewholder

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventItemListBinding
import com.mju.csmoa.home.event_item.adpater.EventItemRecyclerAdapter
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.RecyclerViewDecoration

// 행사상품 리스트 화면
class EventItemListViewHolder(
    private val context: Context,
    private val itemEventItemListBinding: ItemEventItemListBinding
) :
    RecyclerView.ViewHolder(itemEventItemListBinding.root) {


    fun setEventItemList(eventItemList: List<ItemEventItem>) {

        val eventItemRecyclerAdapter = EventItemRecyclerAdapter(context, eventItemList)
        itemEventItemListBinding.recyclerViewItemEventItemListEventItemList.apply {
            adapter = eventItemRecyclerAdapter
            addItemDecoration(RecyclerViewDecoration(0, 30, 20, 20))
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            // 리사이클러뷰 스크롤 감지
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (itemEventItemListBinding.recyclerViewItemEventItemListEventItemList.layoutManager
                                as LinearLayoutManager).findLastVisibleItemPosition()

                    Log.d(TAG, "EventItemListViewHolder -onScrolled() called / lastVisibleItemPosition = $lastVisibleItemPosition")

                    // 스크롤이 끝에 도달했는지 확인
                    if (!itemEventItemListBinding.recyclerViewItemEventItemListEventItemList
                            .canScrollVertically(1)
                    ) {

                    }
                }

            })
        }

    }


}