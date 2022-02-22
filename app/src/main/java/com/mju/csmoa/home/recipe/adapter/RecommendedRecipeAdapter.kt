package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.recipe.viewholder.RecommendedRecipeViewHolder

class RecommendedRecipeAdapter(
    private val recommendRecipes: List<Recipe>,
    private val onRecipeClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<RecommendedRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecommendedRecipeViewHolder(parent, onRecipeClicked)

    override fun onBindViewHolder(holder: RecommendedRecipeViewHolder, position: Int) {
        holder.bind(recommendRecipes[position])
    }

    override fun getItemCount() = recommendRecipes.size
}