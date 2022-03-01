package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemAddIngredientBinding
import com.mju.csmoa.databinding.ItemIngredientBinding
import com.mju.csmoa.home.recipe.domain.model.Ingredient
import com.mju.csmoa.common.util.MyApplication

class IngredientViewHolder(
    parent: ViewGroup,
    onDeleteClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemIngredientBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemIngredientBinding.bind(itemView)

    init {
        binding.imageViewIngredientDelete.setOnClickListener {
            onDeleteClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(ingredient: Ingredient) {
        binding.textViewIngredientName.text = ingredient.name
        binding.textViewIngredientPrice.text = ingredient.price.toString()

        val resourceId = MyApplication.getCsTextBrandResourceId(ingredient.csBrand)
        binding.imageViewIngredientCsBrand.setImageResource(resourceId)
    }
}

class AddIngredientViewHolder(parent: ViewGroup, onAddClicked: () -> Unit) :
    RecyclerView.ViewHolder(
        ItemAddIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {
    private val binding = ItemAddIngredientBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onAddClicked.invoke() }
    }
}




