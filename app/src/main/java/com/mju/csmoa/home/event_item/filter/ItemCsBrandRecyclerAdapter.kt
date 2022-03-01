package com.mju.csmoa.home.event_item.filter

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCsBrandBinding

class ItemCsBrandRecyclerAdapter(
    private val itemCsBrandList: List<ItemCsBrand>,
    private val filterItemClickListener: FilterItemClickListener
) : RecyclerView.Adapter<ItemCsBrandViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemCsBrandViewHolder(parent)

    override fun onBindViewHolder(holder: ItemCsBrandViewHolder, position: Int) {
        holder.bind(itemCsBrandList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemCsBrandList.size
}

class ItemCsBrandViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        ItemCsBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemCsBrandBinding.bind(itemView)

    fun bind(itemCsBrand: ItemCsBrand, filterItemClickListener: FilterItemClickListener) {

        val saturationValue: Float
        val strokeColor: Int

        if (itemCsBrand.isClicked) { // 클릭이 이미 된 상태면
            saturationValue = 1F
            strokeColor = Color.parseColor(itemCsBrand.csColor)
        } else {
            saturationValue = 0F
            strokeColor = Color.GRAY
        }

        with(binding) {

            imageViewCsBrandSelectBrand.setImageResource(itemCsBrand.csImageResourceId)
            cardViewCsBrandContainer.strokeColor = strokeColor
            imageViewCsBrandSelectBrand.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })

            // when button clicked, black and white -> color, color -> black and white
            root.setOnClickListener {
                itemCsBrand.isClicked = !itemCsBrand.isClicked

                // 필터 클릭됐다고 BottomSheetDialog에 전달
                filterItemClickListener.setOnFilterClicked(
                    selectedFilterName = itemCsBrand.brand,
                    position = absoluteAdapterPosition,
                    isClicked = itemCsBrand.isClicked
                )
            }
        }
    }
}

data class ItemCsBrand(
    val brand: String,
    val csImageResourceId: Int,
    val csColor: String,
    var isClicked: Boolean
)