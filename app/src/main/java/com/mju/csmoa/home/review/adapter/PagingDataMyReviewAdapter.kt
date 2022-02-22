package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.MyReviewViewHolder

class PagingDataMyReviewAdapter (private val onMyReviewClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Review, MyReviewViewHolder>(ReviewDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyReviewViewHolder(parent, onMyReviewClicked)

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}