package com.mju.csmoa.home.review.viewholder

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.internal.ViewUtils.dpToPx
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemSealedBestReviewsBinding
import com.mju.csmoa.home.review.adapter.BestReviewAdapter
import com.mju.csmoa.home.review.domain.model.Review
import kotlin.math.abs

class SealedBestReviewsViewHolder(
    parent: ViewGroup,
    private val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemSealedBestReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {
    private val binding = ItemSealedBestReviewsBinding.bind(itemView)

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
        binding.viewpager2SealedBestReviewsBestReviews.setPageTransformer(pageTransformer)

        // The ItemDecoration gives the current (centered) item horizontal margin so that
        // it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            parent.context,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewpager2SealedBestReviewsBestReviews.addItemDecoration(itemDecoration)
    }

    fun bind(bestReviews: List<List<Review>>) {

        val bestReviewAdapter = BestReviewAdapter(bestReviews, bestReviewOnClicked)
        binding.viewpager2SealedBestReviewsBestReviews.apply {
            adapter = bestReviewAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.dotsIndicatorSealedBestReviewsIndicator.setViewPager2(this)
            offscreenPageLimit = 1
        }
    }
}

/**
 * Adds margin to the left and right sides of the RecyclerView item.
 * Adapted from https://stackoverflow.com/a/27664023/4034572
 * @param horizontalMarginInDp the margin resource, in dp.
 */
class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
    RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int =
        context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }

}