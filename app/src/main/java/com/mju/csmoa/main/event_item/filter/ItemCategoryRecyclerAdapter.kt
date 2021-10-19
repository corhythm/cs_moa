package com.mju.csmoa.main.event_item.filter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCategoryBinding

class ItemCategoryRecyclerAdapter : RecyclerView.Adapter<ItemCategoryViewHolder>() {

    private lateinit var itemCategoryList: List<ItemCategory>

    fun submitList(itemCategoryList: List<ItemCategory>) {
        this.itemCategoryList = itemCategoryList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryViewHolder {
        return ItemCategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.bind(itemCategoryList[position])
    }

    override fun getItemCount() = itemCategoryList.size

}

class ItemCategoryViewHolder(private val itemCategoryBinding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(itemCategoryBinding.root) {

    private var isClicked = false
    private var saturationValue = 0F

    fun bind(itemCategory: ItemCategory) {

        itemCategoryBinding.imageViewItemCategoryItemImg.setImageResource(itemCategory.backgroundResourceId)
        itemCategoryBinding.imageViewItemCategoryItemImg.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        itemCategoryBinding.textViewItemCategoryItemName.text = itemCategory.categoryType

        itemCategoryBinding.root.setOnClickListener {
            isClicked = !isClicked
            saturationValue = if (isClicked) 1F else 0F
            itemCategoryBinding.imageViewItemCategoryItemImg.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
        }
    }
}

data class ItemCategory(val backgroundResourceId: Int, val categoryType: String)