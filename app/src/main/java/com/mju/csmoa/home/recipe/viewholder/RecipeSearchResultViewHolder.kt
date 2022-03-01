package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemRecipeSearchResultBinding
import com.mju.csmoa.home.recipe.domain.model.Recipe

class RecipeSearchResultViewHolder(
    private val parent: ViewGroup,
    onRecipeClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemRecipeSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {

    private val binding = ItemRecipeSearchResultBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener { onRecipeClicked(absoluteAdapterPosition) }
    }

    fun bind(recipe: Recipe) {
        with(binding) {
            // 이미지 개수에 따라서 이미지뷰 로딩
            when (recipe.recipeImageUrls.size) {
                1 -> {
                    // type2, type3 container -> invisible
                    cardViewRecipeSearchResultType2Container.visibility = View.INVISIBLE
                    cardViewRecipeSearchResultType3Container.visibility = View.INVISIBLE
                    // type1 -> visible
                    imageViewRecipeSearchResultType1Image1.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType1Image1)
                }
                2 -> {
                    // type1, type3 -> invisible
                    imageViewRecipeSearchResultType1Image1.visibility = View.INVISIBLE
                    cardViewRecipeSearchResultType3Container.visibility = View.INVISIBLE
                    // type2 -> visible
                    cardViewRecipeSearchResultType2Container.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType2Image1)

                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[1])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType2Image2)
                }
                else -> { // 이미지 3개 이상
                    // type1, type3 -> invisible
                    imageViewRecipeSearchResultType1Image1.visibility = View.INVISIBLE
                    cardViewRecipeSearchResultType2Container.visibility = View.INVISIBLE
                    // type2 -> visible
                    cardViewRecipeSearchResultType3Container.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType3Image1)

                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[1])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType3Image2)

                    Glide.with(parent.context)
                        .load(recipe.recipeImageUrls[2])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewRecipeSearchResultType3Image3)
                }
            }

            textViewRecipeSearchResultRecipeName.text = recipe.recipeName // 리뷰 제목
            textViewRecipeSearchResultCreatedAt.text = recipe.createdAt // 생성일
            textViewRecipeSearchResultViewNum.text = recipe.viewNum.toString() // 조회수
            textViewRecipeSearchResultLikeNum.text = recipe.likeNum.toString() // 좋아요 개수

            // check isLike
            if (recipe.isLike) {
                binding.imageViewRecipeSearchResultLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                binding.imageViewRecipeSearchResultLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }
        }
    }

}
