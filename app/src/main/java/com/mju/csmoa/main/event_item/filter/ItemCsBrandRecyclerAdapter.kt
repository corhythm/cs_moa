package com.mju.csmoa.main.event_item.filter

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCsBrandBinding


class ItemCsBrandRecyclerAdapter : RecyclerView.Adapter<ItemCsBrandViewHolder>() {

    private lateinit var itemCsBrandList: List<ItemCsBrand>

    fun submitList(itemCsBrandList: List<ItemCsBrand>) {
        this.itemCsBrandList = itemCsBrandList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCsBrandViewHolder {
        return ItemCsBrandViewHolder(
            ItemCsBrandBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemCsBrandViewHolder, position: Int) {
        holder.bind(itemCsBrandList[position])
    }

    override fun getItemCount() = itemCsBrandList.size

}

class ItemCsBrandViewHolder(private val itemCsBrandBinding: ItemCsBrandBinding) :
    RecyclerView.ViewHolder(itemCsBrandBinding.root) {

    private var isClicked = false
    private var saturationValue = 0F
    private var strokeColor = Color.GRAY

    fun bind(itemCsBrand: ItemCsBrand) {
        itemCsBrandBinding.imageViewCsBrandSelectBrand.setImageResource(itemCsBrand.csImageResourceId)
        itemCsBrandBinding.cardViewCsBrandContainer.strokeColor = strokeColor
        itemCsBrandBinding.imageViewCsBrandSelectBrand.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

        itemCsBrandBinding.root.setOnClickListener {
            isClicked = !isClicked
            saturationValue = if (isClicked) 1F else 0F
            strokeColor = if (isClicked) Color.parseColor(itemCsBrand.csColor) else Color.GRAY
            itemCsBrandBinding.imageViewCsBrandSelectBrand.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
            itemCsBrandBinding.cardViewCsBrandContainer.strokeColor = strokeColor
        }
    }
}

data class ItemCsBrand(val brand: String, val csImageResourceId: Int, val csColor: String)