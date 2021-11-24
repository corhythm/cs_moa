package com.mju.csmoa.home.review.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailedReviewImageBinding
import com.mju.csmoa.util.Constants.TAG

class DetailedReviewImageViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemDetailedReviewImageBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemDetailedReviewImageBinding.bind(itemView)

    fun bind(reviewImageUrl: String) {
        Log.d(TAG, "reviewImageUrl = $reviewImageUrl")
        Glide.with(parent.context)
            .load(reviewImageUrl)
            .placeholder(R.drawable.ic_all_loading)
            .error(R.drawable.ic_all_404)
            .fallback(R.drawable.ic_all_404)
            .into(binding.imageViewDetailedReviewImageReviewImage)
    }
}