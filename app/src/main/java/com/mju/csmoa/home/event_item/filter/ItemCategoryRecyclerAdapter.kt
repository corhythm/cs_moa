package com.mju.csmoa.home.event_item.filter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCategoryBinding

class ItemCategoryRecyclerAdapter(
    private val itemCategoryList: List<ItemCategory>,
    private val filterItemClickListener: FilterItemClickListener
) : RecyclerView.Adapter<ItemCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemCategoryViewHolder(parent)


    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.bind(itemCategoryList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemCategoryList.size

}

class ItemCategoryViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemCategoryBinding.bind(itemView)

    fun bind(itemCategory: ItemCategory, filterItemClickListener: FilterItemClickListener) {

        val saturationValue = if (itemCategory.isClicked) 1F else 0F

        with(binding) {
            imageViewItemCategoryItemImg.setImageResource(itemCategory.backgroundResourceId)
            imageViewItemCategoryItemImg.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
            textViewItemCategoryItemName.text = itemCategory.categoryType

            root.setOnClickListener {
                itemCategory.isClicked = !itemCategory.isClicked

                filterItemClickListener.setOnFilterClicked(
                    selectedFilterName = itemCategory.categoryType,
                    position = absoluteAdapterPosition,
                    isClicked = itemCategory.isClicked
                )
            }
        }
    }

}

data class ItemCategory(
    val backgroundResourceId: Int,
    val categoryType: String,
    var isClicked: Boolean
)