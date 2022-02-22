package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.review.adapter.ReviewDiffUtilCallback
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.ReviewSearchResultViewHolder

// PagingDataReviewAdapter랑 합칠 수 있을 거 같긴 한데....
class PagingDataReviewSearchResultAdapter(private val onReviewClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Review, ReviewSearchResultViewHolder>(ReviewDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReviewSearchResultViewHolder(parent, onReviewClicked)

    override fun onBindViewHolder(holder: ReviewSearchResultViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}