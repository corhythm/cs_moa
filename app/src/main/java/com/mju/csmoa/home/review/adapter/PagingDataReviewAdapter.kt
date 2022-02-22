package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.ReviewViewHolder


class PagingDataReviewAdapter(private val onReviewClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Review, ReviewViewHolder>(ReviewDiffUtilCallback()) {

    override fun getItemViewType(position: Int) = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReviewViewHolder(parent, onReviewClicked)

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ReviewDiffUtilCallback : DiffUtil.ItemCallback<Review>() {

    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId == newItem.reviewId &&
                oldItem.reviewName == newItem.reviewName
    }
}