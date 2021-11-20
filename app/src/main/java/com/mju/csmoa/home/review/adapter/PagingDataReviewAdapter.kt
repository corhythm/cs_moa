package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.ReviewViewHolder


class PagingDataReviewAdapter() : PagingDataAdapter<Review, ReviewViewHolder>(DiffUtilCallback()) {

    companion object {
        const val MVP_REVIEW = 0 // 이번주 MVP 리뷰
        const val NORMAL_REVIEW = 1 // 일반 리뷰
    }

    override fun getItemViewType(position: Int) = NORMAL_REVIEW

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReviewViewHolder(parent)

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<Review>() {

    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId == newItem.reviewId &&
                oldItem.itemName == newItem.itemName
    }

}