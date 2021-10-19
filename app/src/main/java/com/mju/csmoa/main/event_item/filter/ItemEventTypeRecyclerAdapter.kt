package com.mju.csmoa.main.event_item.filter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventTypeBinding


class ItemEventTypeRecyclerAdapter : RecyclerView.Adapter<ItemEventTypeViewHolder>() {

    private lateinit var itemEventTypeList: List<ItemEventType>
    private lateinit var filterItemClickListener: FilterItemClickListener
    private val itemEventTypeViewHolderList = ArrayList<ItemEventTypeViewHolder?>()

    fun submitList(itemEventTypeList: List<ItemEventType>) {
        this.itemEventTypeList = itemEventTypeList
    }

    fun setFilterListener(filterItemClickListener: FilterItemClickListener) {
        this.filterItemClickListener = filterItemClickListener
    }

    fun reset() {
        itemEventTypeViewHolderList.forEach { itemEventTypeViewHolder ->
            itemEventTypeViewHolder?.reset()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemEventTypeViewHolder {
        val itemEventTypeViewHolder = ItemEventTypeViewHolder(
            ItemEventTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        itemEventTypeViewHolderList.add(itemEventTypeViewHolder)
        return itemEventTypeViewHolder
    }

    override fun onBindViewHolder(holder: ItemEventTypeViewHolder, position: Int) {
        holder.bind(itemEventTypeList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemEventTypeList.size

}

class ItemEventTypeViewHolder(private val itemEventTypeBinding: ItemEventTypeBinding) :
    RecyclerView.ViewHolder(itemEventTypeBinding.root) {

    private var isClicked = false
    private var textAndStrokeColor = Color.GRAY

    fun bind(itemEventType: ItemEventType, filterItemClickListener: FilterItemClickListener) {
        itemEventTypeBinding.textViewEventTypeType.text = itemEventType.eventType
        itemEventTypeBinding.textViewEventTypeType.setTextColor(textAndStrokeColor)
        itemEventTypeBinding.cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor

        itemEventTypeBinding.root.setOnClickListener {
            isClicked = !isClicked
            textAndStrokeColor = if (isClicked) Color.parseColor(itemEventType.textAndStrokeColor) else Color.GRAY
            itemEventTypeBinding.textViewEventTypeType.setTextColor(textAndStrokeColor)
            itemEventTypeBinding.cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor

            filterItemClickListener.setOnFilterClicked(itemEventType.eventType, isClicked)
        }
    }

    fun reset() {
        isClicked = false
        textAndStrokeColor = Color.GRAY
        itemEventTypeBinding.textViewEventTypeType.setTextColor(textAndStrokeColor)
        itemEventTypeBinding.cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor
    }
}

data class ItemEventType(val eventType: String, val textAndStrokeColor: String)