package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.viewholder.DetailedReviewImageViewHolder

class DetailedReviewImageAdapter(private val reviewImages: List<String>) : RecyclerView.Adapter<DetailedReviewImageViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DetailedReviewImageViewHolder(parent)

    override fun onBindViewHolder(holder: DetailedReviewImageViewHolder, position: Int) {
        holder.bind(reviewImages[position])
    }

    override fun getItemCount() = reviewImages.size
}