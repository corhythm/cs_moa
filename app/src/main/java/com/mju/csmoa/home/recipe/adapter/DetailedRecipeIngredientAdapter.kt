package com.mju.csmoa.home.recipe.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.recipe.domain.model.Ingredient
import com.mju.csmoa.home.recipe.viewholder.DetailedRecipeIngredientViewHolder

class DetailedRecipeIngredientAdapter(
    private val ingredients: List<Ingredient>,
    private val goToMapClicked: (anchorView: View, csBrand: String) -> Unit
) : RecyclerView.Adapter<DetailedRecipeIngredientViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DetailedRecipeIngredientViewHolder(parent, goToMapClicked)

    override fun onBindViewHolder(holder: DetailedRecipeIngredientViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size
}