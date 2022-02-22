package com.mju.csmoa.home.review.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.home.review.viewholder.DetailedReviewViewHolder

class DetailedReviewAdapter(
    private val detailedReview: DetailedReview,
    private val onLikeClicked: () -> Unit,
    private val goToMapClicked: (anchorView: View, csBrand: String) -> Unit
) :
    RecyclerView.Adapter<DetailedReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DetailedReviewViewHolder(parent, onLikeClicked, goToMapClicked)

    override fun onBindViewHolder(holder: DetailedReviewViewHolder, position: Int) {
        holder.bind(detailedReview)
    }

    override fun getItemCount() = 1
}