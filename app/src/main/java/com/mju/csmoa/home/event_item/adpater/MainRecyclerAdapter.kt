package com.mju.csmoa.home.event_item.adpater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventItemListBinding
import com.mju.csmoa.databinding.ItemRecommendedEventItemListBinding
import com.mju.csmoa.home.event_item.domain.model.AllEventItem
import com.mju.csmoa.home.event_item.domain.model.AllEventItem.Companion.EVENT_ITEM_TYPE
import com.mju.csmoa.home.event_item.domain.model.AllEventItem.Companion.RECOMMENDED_TYPE
import com.mju.csmoa.home.event_item.viewholder.nested_viewholder.EventItemListViewHolder
import com.mju.csmoa.home.event_item.viewholder.nested_viewholder.RecommendedEventItemListViewHolder
import com.mju.csmoa.util.Constants.TAG

class MainRecyclerAdapter(
    private val context: Context,
    private val allEventItemList: List<AllEventItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return allEventItemList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.d(TAG, "MainRecyclerAdapter -onCreateViewHolder() called / viewType = $viewType")
        return when (viewType) {
            RECOMMENDED_TYPE -> {

                RecommendedEventItemListViewHolder(
                    context = context,
                    ItemRecommendedEventItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            EVENT_ITEM_TYPE -> {

                EventItemListViewHolder(
                    context = context,
                    itemEventItemListBinding = ItemEventItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                throw IllegalStateException("Unknown View")
            }
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            // 추천 행사 상품 리스트
            is RecommendedEventItemListViewHolder -> {
                holder.setRecommendedEventItemList(
                    allEventItemList[position].recommendedEventItemList ?: ArrayList()
                )
            }

            // 일반 행사 상품 리스트
            is EventItemListViewHolder -> {
                holder.setEventItemList(
                    allEventItemList[position].eventItemList ?: ArrayList()
                )
            }
        }


    }

    override fun getItemCount(): Int = allEventItemList.size
}




