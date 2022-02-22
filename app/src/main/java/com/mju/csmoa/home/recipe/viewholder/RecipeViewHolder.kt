package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.databinding.ItemRecipeBinding
import com.mju.csmoa.home.recipe.domain.model.Recipe

class RecipeViewHolder(
    private val parent: ViewGroup,
    private val onRecipeClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemRecipeBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemRecipeBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener { onRecipeClicked(absoluteAdapterPosition) }
    }

    fun bind(recipe: Recipe) {
        with(binding) {
            Glide.with(parent.context)
                .load(recipe.recipeImageUrls[0])
                .placeholder(com.mju.csmoa.R.drawable.ic_all_loading)
                .error(com.mju.csmoa.R.drawable.ic_all_404)
                .fallback(com.mju.csmoa.R.drawable.ic_all_404)
                .centerCrop()
                .into(imageViewRecipeRecipeImage)

            // 재료 문자열 하나로 합치기
            textViewRecipeRecipeName.text = recipe.recipeName // 레시피 이름
            textViewRecipeIngredients.text = recipe.ingredients // 레시피 재료
            textViewRecipeLike.text = recipe.likeNum.toString() // 좋아요 개수

            if (recipe.isLike) { // 좋아요 했으면
                imageViewRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_filledheart)
            } else {
                imageViewRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_empty_stroke_colored_heart)
            }

        }
    }
}