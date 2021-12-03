package com.mju.csmoa.home.recipe.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecommendedRecipeBinding
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.util.MyApplication

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
                .load(recipe.recipeMainImageUrl)
                .placeholder(com.mju.csmoa.R.drawable.ic_all_loading)
                .error(com.mju.csmoa.R.drawable.ic_all_404)
                .fallback(com.mju.csmoa.R.drawable.ic_all_404)
                .centerCrop()
                .into(imageViewRecommendedRecipeRecipeImage)

            // 재료 문자열 하나로 합치기
            val ingredient = ""
            recipe.ingredients.forEach {
                 ingredient.plus("${it.name} + ") // 마지막 + 모양 제거해 줘야 함
            }
            textViewRecommendedRecipeIngredients.text = ingredient
            textViewRecommendedRecipeLike.text = recipe.likeNum.toString() // 좋아요 개수

            if (recipe.isLike) { // 좋아요 했으면
                imageViewRecommendedRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_filledheart)
            } else {
                imageViewRecommendedRecipeLikeImage.setImageResource(com.mju.csmoa.R.drawable.ic_all_empty_stroke_colored_heart)
            }

        }
    }
}