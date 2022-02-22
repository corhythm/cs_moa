package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.recipe.domain.model.Ingredient
import com.mju.csmoa.home.recipe.viewholder.AddIngredientViewHolder
import com.mju.csmoa.home.recipe.viewholder.IngredientViewHolder

class AddIngredientAdapter(
    private val ingredients: List<Ingredient>,
    private val onAddClicked: () -> Unit,
    private val onDeleteClicked: (position: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ADD = 0
        const val INGREDIENT = 1
    }

    override fun getItemViewType(position: Int) =
        if (ingredients[position].name == "" && ingredients[position].price == "" &&
            ingredients[position].csBrand == "") ADD else INGREDIENT


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ADD) AddIngredientViewHolder(parent, onAddClicked)
        else IngredientViewHolder(parent, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IngredientViewHolder) {
            holder.bind(ingredients[position])
        }
    }

    override fun getItemCount() = ingredients.size
}