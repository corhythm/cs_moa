package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.home.review.viewholder.DetailedReviewViewHolder

class DetailedReviewAdapter(private val detailedReview: DetailedReview) :
    RecyclerView.Adapter<DetailedReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DetailedReviewViewHolder(parent)

    override fun onBindViewHolder(holder: DetailedReviewViewHolder, position: Int) {
        holder.bind(detailedReview)
    }

    override fun getItemCount() = 1
}