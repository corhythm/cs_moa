package com.mju.csmoa.main.event_item.filter

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCsBrandBinding


class ItemCsBrandRecyclerAdapter : RecyclerView.Adapter<ItemCsBrandViewHolder>() {

    private lateinit var itemCsBrandList: List<ItemCsBrand>
    private lateinit var filterItemClickListener: FilterItemClickListener
    private val itemCsBrandViewHolderList = ArrayList<ItemCsBrandViewHolder?>()

    fun submitList(itemCsBrandList: List<ItemCsBrand>) {
        this.itemCsBrandList = itemCsBrandList
    }

    fun setFilterListener(filterItemClickListener: FilterItemClickListener) {
        this.filterItemClickListener = filterItemClickListener
    }

    fun reset() {
        itemCsBrandViewHolderList.forEach { itemCsBrandViewHolder ->
            itemCsBrandViewHolder?.reset()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCsBrandViewHolder {
        Log.d("로그", "ItemCsBrandRecyclerAdapter -onCreateViewHolder() called")
        val itemCsBrandViewHolder = ItemCsBrandViewHolder(
            ItemCsBrandBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        itemCsBrandViewHolderList.add(itemCsBrandViewHolder)
        return itemCsBrandViewHolder
    }

    override fun onBindViewHolder(holder: ItemCsBrandViewHolder, position: Int) {
        holder.bind(itemCsBrandList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemCsBrandList.size

}

class ItemCsBrandViewHolder(private val itemCsBrandBinding: ItemCsBrandBinding) :
    RecyclerView.ViewHolder(itemCsBrandBinding.root) {

    private var isClicked = false
    private var saturationValue = 0F
    private var strokeColor = Color.GRAY

    fun bind(itemCsBrand: ItemCsBrand, filterItemClickListener: FilterItemClickListener) {
        itemCsBrandBinding.imageViewCsBrandSelectBrand.setImageResource(itemCsBrand.csImageResourceId)
        itemCsBrandBinding.cardViewCsBrandContainer.strokeColor = strokeColor
        itemCsBrandBinding.imageViewCsBrandSelectBrand.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

        // when button clicked, black and white -> color, color -> black and white
        itemCsBrandBinding.root.setOnClickListener {
            isClicked = !isClicked
            saturationValue = if (isClicked) 1F else 0F
            strokeColor = if (isClicked) Color.parseColor(itemCsBrand.csColor) else Color.GRAY
            itemCsBrandBinding.imageViewCsBrandSelectBrand.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
            itemCsBrandBinding.cardViewCsBrandContainer.strokeColor = strokeColor

            // 필터 클릭됐다고 BottomSheetDialog에 전달
            filterItemClickListener.setOnFilterClicked(itemCsBrand.brand, isClicked)
        }
    }

    fun reset() {
        isClicked = false
        saturationValue = 0F
        strokeColor = Color.GRAY
        itemCsBrandBinding.imageViewCsBrandSelectBrand.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
        itemCsBrandBinding.cardViewCsBrandContainer.strokeColor = strokeColor
    }
}

data class ItemCsBrand(val brand: String, val csImageResourceId: Int, val csColor: String)