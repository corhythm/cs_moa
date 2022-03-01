package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemReviewSearchResultBinding
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.common.util.MyApplication

class ReviewSearchResultViewHolder(
    private val parent: ViewGroup,
    onReviewClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemReviewSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {

    private val binding = ItemReviewSearchResultBinding.bind(super.itemView)

    init {
        binding.root.setOnClickListener { onReviewClicked(absoluteAdapterPosition) }
    }

    fun bind(review: Review) {
        with(binding) {
            // 이미지 개수에 따라서 이미지뷰 로딩
            when (review.reviewImageUrls.size) {
                1 -> {
                    // type2, type3 container -> invisible
                    cardViewReviewSearchResultType2Container.visibility = View.INVISIBLE
                    cardViewReviewSearchResultType3Container.visibility = View.INVISIBLE
                    // type1 -> visible
                    imageViewReviewSearchResultType1Image1.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType1Image1)
                }
                2 -> {
                    // type1, type3 -> invisible
                    imageViewReviewSearchResultType1Image1.visibility = View.INVISIBLE
                    cardViewReviewSearchResultType3Container.visibility = View.INVISIBLE
                    // type2 -> visible
                    cardViewReviewSearchResultType2Container.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType2Image1)

                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[1])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType2Image2)
                }
                else -> { // 이미지 3개 이상
                    // type1, type3 -> invisible
                    imageViewReviewSearchResultType1Image1.visibility = View.INVISIBLE
                    cardViewReviewSearchResultType2Container.visibility = View.INVISIBLE
                    // type2 -> visible
                    cardViewReviewSearchResultType3Container.visibility = View.VISIBLE
                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[0])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType3Image1)

                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[1])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType3Image2)

                    Glide.with(parent.context)
                        .load(review.reviewImageUrls[2])
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.ic_all_404)
                        .fallback(R.drawable.ic_all_404)
                        .into(imageViewReviewSearchResultType3Image3)
                }
            }

            textViewReviewSearchResultReviewName.text = review.reviewName // 리뷰 제목
            textViewReviewSearchResultCreatedAt.text = review.createdAt // 생성일
            textViewReviewSearchResultStarScore.text = review.starScore.toString() // 별점
            textViewReviewSearchResultComment.text = review.commentNum.toString() // 댓글 개수
            textViewReviewSearchResultViewNum.text = review.viewNum.toString() // 조회수
            textViewReviewSearchResultLikeNum.text = review.likeNum.toString() // 좋아요 개수
            textViewReviewSearchResultPrice.text = review.price // 가격

            val csBrandImageResourceId = MyApplication.getCsTextBrandResourceId(review.csBrand)
            imageViewReviewSearchResultCsBrand.setImageResource(csBrandImageResourceId) // 편의점 브랜드 이미지

            // check isLike
            if (review.isLike) {
                binding.imageViewReviewSearchResultLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                binding.imageViewReviewSearchResultLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }
        }
    }

}