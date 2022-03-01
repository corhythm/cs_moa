package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemDetailedRecipeIngredientBinding
import com.mju.csmoa.home.recipe.domain.model.Ingredient
import com.mju.csmoa.common.util.MyApplication

class DetailedRecipeIngredientViewHolder(
    parent: ViewGroup,
    private val goToMapClicked: (anchorView: View, csBrand: String) -> Unit
) : RecyclerView.ViewHolder(
    ItemDetailedRecipeIngredientBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemDetailedRecipeIngredientBinding.bind(super.itemView)

    fun bind(ingredient: Ingredient) {
        with(binding) {
            // 편의점 마크 클릭하면 지도로 이동
            imageViewDetailedRecipeIngredientCsBrand.setOnClickListener {
                goToMapClicked(it, ingredient.csBrand)
            }
            textViewDetailedRecipeIngredientIngredientNameAndPrice.text =
                "${ingredient.name} - ${ingredient.price}"

            val csBrandResourceId = MyApplication.getCsTextBrandResourceId(ingredient.csBrand)
            imageViewDetailedRecipeIngredientCsBrand.setImageResource(csBrandResourceId)
        }


    }
}