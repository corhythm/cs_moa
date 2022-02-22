package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.home.review.viewholder.BestReviewViewHolder

class BestReviewAdapter(
    private val bestReviews: List<List<Review>>,
    private val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit
) :
    RecyclerView.Adapter<BestReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BestReviewViewHolder(parent, bestReviewOnClicked)

    override fun onBindViewHolder(holder: BestReviewViewHolder, position: Int) {
        holder.bind(bestReviews[position])
    }

    override fun getItemCount() = bestReviews.size
}