package com.mju.csmoa.home.review.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemReviewBinding
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.common.util.MyApplication
import java.util.*

class ReviewViewHolder(
    private val parent: ViewGroup,
    private val onReviewClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemReviewBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemReviewBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onReviewClicked(absoluteAdapterPosition) }
    }

    fun bind(review: Review?) {
        val random = Random()

        with(binding) {
            Glide.with(parent.context)
                .load(review!!.reviewImageUrls[0])
                .fitCenter()
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .into(imageViewReviewItemImage)

            textViewReviewItemName.text = review.reviewName
            textViewReviewCommentNum.text = review.commentNum.toString()
            textViewReviewLikeNum.text = review.likeNum.toString()
            textViewReviewRating.text = "(${review.starScore})"
            ratingBarReviewReviewRating.rating = review.starScore
            textViewReviewPrice.text = review.price
            textViewReviewViewNum.text = review.viewNum.toString()
            textViewReviewCreatedAt.text = review.createdAt

            if (review.isLike) { // 좋아요 했으면
                imageViewReviewLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewReviewLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            val color: Int =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            cardViewReviewImageContainer.strokeColor = color

            // 편의점 브랜드 로고 설정
            val csBrandStrokeColor = MyApplication.getCsBrandColor(review.csBrand)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(review.csBrand)

            if (csBrandResourceId == -1 &&  csBrandStrokeColor == -1) { // 편의점 브랜드가 기타이면
                imageViewReviewCsBrand.visibility = View.INVISIBLE
                textViewReviewCsBrandEtc.visibility = View.VISIBLE
            } else {
                // 편의점 브랜드 설정
                cardViewReviewCsBrandContainer.strokeColor = csBrandStrokeColor
                imageViewReviewCsBrand.setImageResource(csBrandResourceId)
            }

        }
    }
}