package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.ItemReviewBinding
import com.mju.csmoa.home.review.domain.model.Review

class ReviewViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemReviewBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {

    fun bind(review: Review?) {

    }
}