package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemMyReviewBinding
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.common.util.MyApplication

class MyReviewViewHolder(
    private val parent: ViewGroup,
    onMyReviewClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    ) {
    private val binding = ItemMyReviewBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener { onMyReviewClicked(absoluteAdapterPosition) }
    }

    fun bind(review: Review) {
        with(binding) {

            Glide.with(parent.context)
                .load(review.reviewImageUrls[0])
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .into(imageViewMyReviewReviewImage)

            textViewMyReviewReviewName.text = review.reviewName // 리뷰 이름
            textViewMyReviewPrice.text = review.price // 가격
            textViewMyReviewContent.text = review.content // 리뷰 내용
            textViewMyReviewStarScore.text = review.starScore.toString() // 별점
            textViewMyReviewCommentNum.text = review.commentNum.toString() // 댓글 개수
            textViewMyReviewViewNum.text = review.viewNum.toString() // 조회수
            textViewMyReviewLikeNum.text =review.likeNum.toString() // 좋아요 개수
            textViewMyReviewCreatedAt.text = review.createdAt // createdAt
            val csBrandImageResourceId = MyApplication.getCsTextBrandResourceId(review.csBrand)
            imageViewMyReviewCsBrand.setImageResource(csBrandImageResourceId) // csBrand

            if (review.isLike) {
                imageViewMyReviewLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewMyReviewLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }
        }
    }
}