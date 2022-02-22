package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemSealedRecommendedRecipeBinding
import com.mju.csmoa.home.recipe.adapter.RecommendedRecipeAdapter
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.review.viewholder.HorizontalMarginItemDecoration
import kotlin.math.abs

class SealedRecommendedRecipeViewHolder(
    parent: ViewGroup,
    private val onRecipeClicked: (position: Int) -> Unit // 진짜 뷰홀더에게 넘겨줘야 함
) : RecyclerView.ViewHolder(
    ItemSealedRecommendedRecipeBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemSealedRecommendedRecipeBinding.bind(itemView)

    init {
        // pager transform init
        val nextItemVisiblePx =
            parent.context.resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            parent.context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * abs(position))
            // If you want a fading effect uncomment the next line:
//            page.alpha = 0.25f + (1 - abs(position))
        }
        binding.viewpager2SealedRecommendedRecipeRecommendedRecipes.setPageTransformer(pageTransformer)

        // The ItemDecoration gives the current (centered) item horizontal margin so that
        // it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            parent.context,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewpager2SealedRecommendedRecipeRecommendedRecipes.addItemDecoration(itemDecoration)
    }

    fun bind(recommendedRecipes: List<Recipe>) {
        val recommendedAdapter = RecommendedRecipeAdapter(recommendedRecipes, onRecipeClicked)
        binding.viewpager2SealedRecommendedRecipeRecommendedRecipes.apply {
            adapter = recommendedAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.dotsIndicatorSealedRecommendedRecipeIndicator.setViewPager2(this)
            offscreenPageLimit = 1
        }
    }
}