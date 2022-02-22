package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.recipe.viewholder.RecipeViewHolder


class PagingDataRecipeAdapter(private val onRecipeClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Recipe, RecipeViewHolder>(RecipeDiffUtilCallback()) {

    override fun getItemViewType(position: Int) = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecipeViewHolder(parent, onRecipeClicked)

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}

class RecipeDiffUtilCallback : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.recipeId == newItem.recipeId
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.recipeId == newItem.recipeId &&
                oldItem.recipeName == newItem.recipeName
    }
}