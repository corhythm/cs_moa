package com.mju.csmoa.home.event_item.filter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventTypeBinding
import com.mju.csmoa.util.Constants.TAG


class ItemEventTypeRecyclerAdapter(
    private val itemEventTypeList: List<ItemEventType>,
    private val filterItemClickListener: FilterItemClickListener
) : RecyclerView.Adapter<ItemEventTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemEventTypeViewHolder(parent)

    override fun onBindViewHolder(holder: ItemEventTypeViewHolder, position: Int) {
        holder.bind(itemEventTypeList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemEventTypeList.size

}

class ItemEventTypeViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        ItemEventTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemEventTypeBinding.bind(itemView)

    fun bind(itemEventType: ItemEventType, filterItemClickListener: FilterItemClickListener) {

        Log.d(TAG, "ItemEventTypeViewHolder -bind() called / itemEventType = $itemEventType")

        val textAndStrokeColor = if (itemEventType.isClicked)
            Color.parseColor(itemEventType.textAndStrokeColor)
        else
            Color.GRAY

        with(binding) {
            textViewEventTypeType.text = itemEventType.eventType
            textViewEventTypeType.setTextColor(textAndStrokeColor)
            cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor

            // 뷰홀더 클릭 시
            root.setOnClickListener {
                itemEventType.isClicked = !itemEventType.isClicked

                filterItemClickListener.setOnFilterClicked(
                    selectedFilterName = itemEventType.eventType,
                    position = absoluteAdapterPosition,
                    isClicked = itemEventType.isClicked
                )
            }
        }
    }

}

data class ItemEventType(
    val eventType: String,
    val textAndStrokeColor: String,
    var isClicked: Boolean
)