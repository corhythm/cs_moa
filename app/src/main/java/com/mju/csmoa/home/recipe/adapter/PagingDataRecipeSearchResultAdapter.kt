package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.recipe.viewholder.RecipeSearchResultViewHolder

class PagingDataRecipeSearchResultAdapter(private val onRecipeClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Recipe, RecipeSearchResultViewHolder>(RecipeDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecipeSearchResultViewHolder(parent, onRecipeClicked)

    override fun onBindViewHolder(holder: RecipeSearchResultViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}