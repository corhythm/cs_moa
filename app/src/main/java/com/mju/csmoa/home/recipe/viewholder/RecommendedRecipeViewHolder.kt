package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.databinding.ItemRecommendedRecipeBinding
import com.mju.csmoa.home.recipe.domain.model.Recipe

class RecommendedRecipeViewHolder(
    private val parent: ViewGroup,
    private val onRecipeClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemRecommendedRecipeBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemRecommendedRecipeBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener { onRecipeClicked.invoke(absoluteAdapterPosition) }
    }

    fun bind(recipe: Recipe) {
        with(binding) {
            Glide.with(parent.context)
                .load(recipe.recipeImageUrls[0])
                .placeholder(com.mju.csmoa.R.drawable.ic_all_loading)
                .error(com.mju.csmoa.R.drawable.ic_all_404)
                .fallback(com.mju.csmoa.R.drawable.ic_all_404)
                .centerCrop()
                .into(imageViewRecommendedRecipeRecipeImage)

            textViewRecommendedRecipeRecipeName.text = recipe.recipeName // 레시피 이름
            textViewRecommendedRecipeIngredients.text = recipe.ingredients // 재료
            textViewRecommendedRecipeLike.text = recipe.likeNum.toString() // 좋아요 개수

            if (recipe.isLike) { // 좋아요 했으면
                imageViewRecommendedRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_filledheart)
            } else {
                imageViewRecommendedRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_empty_stroke_colored_heart)
            }

        }
    }
}