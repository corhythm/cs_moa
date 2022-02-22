package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailedReviewOrRecipeImageBinding

class DetailedReviewOrRecipeImageViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemDetailedReviewOrRecipeImageBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemDetailedReviewOrRecipeImageBinding.bind(itemView)

    fun bind(reviewOrRecipeImageUrl: String) {
        Glide.with(parent.context)
            .load(reviewOrRecipeImageUrl)
            .placeholder(R.drawable.ic_all_loading)
            .error(R.drawable.ic_all_404)
            .fallback(R.drawable.ic_all_404)
            .into(binding.imageViewDetailedReviewOrRecipeImageReviewImage)
    }
}