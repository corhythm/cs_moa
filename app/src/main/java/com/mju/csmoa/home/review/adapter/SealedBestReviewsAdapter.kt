package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.SealedBestReviewsViewHolder

class SealedBestReviewsAdapter(
    private val bestReviews: List<List<Review>>,
    private val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit
) :
    RecyclerView.Adapter<SealedBestReviewsViewHolder>() {

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = SealedBestReviewsViewHolder(parent, bestReviewOnClicked)

    override fun onBindViewHolder(holder: SealedBestReviewsViewHolder, position: Int) {
        holder.bind(bestReviews)
    }

    override fun getItemCount() = 1
}