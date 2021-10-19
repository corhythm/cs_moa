package com.mju.csmoa.main.event_item.filter

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemEventTypeBinding


class ItemEventTypeRecyclerAdapter : RecyclerView.Adapter<ItemEventTypeViewHolder>() {

    private lateinit var itemEventTypeList: List<ItemEventType>

    fun submitList(itemEventTypeList: List<ItemEventType>) {
        this.itemEventTypeList = itemEventTypeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemEventTypeViewHolder {
        return ItemEventTypeViewHolder(
            ItemEventTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemEventTypeViewHolder, position: Int) {
        holder.bind(itemEventTypeList[position])
    }

    override fun getItemCount() = itemEventTypeList.size

}

class ItemEventTypeViewHolder(private val itemEventTypeBinding: ItemEventTypeBinding) :
    RecyclerView.ViewHolder(itemEventTypeBinding.root) {

    private var isClicked = false
    private var textAndStrokeColor = Color.GRAY

    fun bind(itemEventType: ItemEventType) {
        itemEventTypeBinding.textViewEventTypeType.text = itemEventType.eventType
        itemEventTypeBinding.textViewEventTypeType.setTextColor(textAndStrokeColor)
        itemEventTypeBinding.cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor

        itemEventTypeBinding.root.setOnClickListener {
            isClicked = !isClicked
            textAndStrokeColor = if (isClicked) Color.parseColor(itemEventType.textAndStrokeColor) else Color.GRAY
            itemEventTypeBinding.textViewEventTypeType.setTextColor(textAndStrokeColor)
            itemEventTypeBinding.cardViewItemEventTypeContainer.strokeColor = textAndStrokeColor
        }
    }
}

data class ItemEventType(val eventType: String, val textAndStrokeColor: String)