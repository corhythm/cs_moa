package com.mju.csmoa.main.event_item.filter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemCategoryBinding

class ItemCategoryRecyclerAdapter : RecyclerView.Adapter<ItemCategoryViewHolder>() {

    private lateinit var itemCategoryList: List<ItemCategory>
    private lateinit var filterItemClickListener: FilterItemClickListener
    private val itemCategoryViewHolderList = ArrayList<ItemCategoryViewHolder?>()

    fun submitList(itemCategoryList: List<ItemCategory>) {
        this.itemCategoryList = itemCategoryList
    }

    fun setFilterListener(filterItemClickListener: FilterItemClickListener) {
        this.filterItemClickListener = filterItemClickListener
    }

    // 아,, 이렇게 하면 안 될 거 같은데
    fun reset() {
        itemCategoryViewHolderList.forEach { itemCategoryViewHolder ->
            itemCategoryViewHolder?.reset()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoryViewHolder {
        val itemCategoryViewHolder = ItemCategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        itemCategoryViewHolderList.add(itemCategoryViewHolder)
        return itemCategoryViewHolder
    }

    override fun onBindViewHolder(holder: ItemCategoryViewHolder, position: Int) {
        holder.bind(itemCategoryList[position], filterItemClickListener)
    }

    override fun getItemCount() = itemCategoryList.size

}

class ItemCategoryViewHolder(private val itemCategoryBinding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(itemCategoryBinding.root) {

    private var isClicked = false
    private var saturationValue = 0F

    fun bind(itemCategory: ItemCategory, filterItemClickListener: FilterItemClickListener) {

        itemCategoryBinding.imageViewItemCategoryItemImg.setImageResource(itemCategory.backgroundResourceId)
        itemCategoryBinding.imageViewItemCategoryItemImg.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        itemCategoryBinding.textViewItemCategoryItemName.text = itemCategory.categoryType

        itemCategoryBinding.root.setOnClickListener {
            isClicked = !isClicked
            saturationValue = if (isClicked) 1F else 0F
            itemCategoryBinding.imageViewItemCategoryItemImg.colorFilter =
                ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })

            filterItemClickListener.setOnFilterClicked(itemCategory.categoryType, isClicked)
        }
    }

    fun reset() {
        isClicked = false
        saturationValue = 0F
        itemCategoryBinding.imageViewItemCategoryItemImg.colorFilter =
            ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(saturationValue) })
    }
}

data class ItemCategory(val backgroundResourceId: Int, val categoryType: String)