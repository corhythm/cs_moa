package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.viewholder.DetailedReviewOrRecipeImageViewHolder

class DetailedReviewOrRecipeImageAdapter(private val reviewImages: List<String>) :
    RecyclerView.Adapter<DetailedReviewOrRecipeImageViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DetailedReviewOrRecipeImageViewHolder(parent)

    override fun onBindViewHolder(holder: DetailedReviewOrRecipeImageViewHolder, position: Int) {
        holder.bind(reviewImages[position])
    }

    override fun getItemCount() = reviewImages.size
}