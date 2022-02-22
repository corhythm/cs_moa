package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.recipe.viewholder.SealedRecommendedRecipeViewHolder

class SealedRecommendedRecipeAdapter(
    private val recommendRecipes: List<Recipe>,
    private val onRecipeClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<SealedRecommendedRecipeViewHolder>() {

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = SealedRecommendedRecipeViewHolder(parent, onRecipeClicked)

    override fun onBindViewHolder(holder: SealedRecommendedRecipeViewHolder, position: Int) {
        holder.bind(recommendRecipes)
    }

    override fun getItemCount() = 1
}