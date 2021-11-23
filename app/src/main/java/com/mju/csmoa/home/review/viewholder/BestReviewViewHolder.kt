package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemBestReviewBinding
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.util.MyApplication

class BestReviewViewHolder(
    private val parent: ViewGroup,
    private val bestReviewOnClicked: (position: Int, rootPosition: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemBestReviewBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {
    private val binding = ItemBestReviewBinding.bind(itemView)

    init {
        binding.layoutBestReviewRootContainer1.root.setOnClickListener {
            bestReviewOnClicked.invoke(
                absoluteAdapterPosition,
                0
            )
        }
        binding.layoutBestReviewRootContainer2.root.setOnClickListener {
            bestReviewOnClicked.invoke(
                absoluteAdapterPosition,
                1
            )
        }
        binding.layoutBestReviewRootContainer3.root.setOnClickListener {
            bestReviewOnClicked.invoke(
                absoluteAdapterPosition,
                2
            )
        }
    }

    fun bind(review: List<Review>) {

        with(binding.layoutBestReviewRootContainer1) {
            Glide.with(parent.context)
                .load(review[0].itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .into(imageViewBestReviewItemImage)

            textViewBestReviewTitle.text = review[0].itemName // 아이템 이름
            textViewBestReviewPrice.text = review[0].itemPrice // 가격
            textViewBestReviewRating.text = review[0].itemStarScore.toString() // 별점
            textViewBestReviewCommentNum.text = review[0].commentNum.toString() // 댓글 개수
            textViewBestReviewLikeNum.text = review[0].likeNum.toString() // 좋아요 개수
            textViewBestReviewCommentNum.text = review[0].commentNum.toString() // 댓글 개수
            textViewBestReviewViewNum.text = review[0].viewNum.toString() // 조회수

            if (review[0].isLike) { // 좋아요 했으면
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            // 편의점 브랜드 로고 설정
            val csBrandStrokeColor = MyApplication.getCsBrandColor(review[0].csBrand)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(review[0].csBrand)

            if (csBrandResourceId == -1 &&  csBrandStrokeColor == -1) { // 편의점 브랜드가 기타이면
                imageViewBestReviewCsBrand.visibility = View.INVISIBLE
                textViewBestReviewCsBrandEtc.visibility = View.INVISIBLE
            } else {
                // 편의점 브랜드 설정
                cardViewBestReviewCsBrandContainer.strokeColor = csBrandStrokeColor
                imageViewBestReviewCsBrand.setImageResource(csBrandResourceId)
            }
        }


        with(binding.layoutBestReviewRootContainer2) {
            Glide.with(parent.context)
                .load(review[1].itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .into(imageViewBestReviewItemImage)

            textViewBestReviewTitle.text = review[1].itemName // 아이템 이름
            textViewBestReviewPrice.text = review[1].itemPrice // 가격
            textViewBestReviewRating.text = review[1].itemStarScore.toString() // 별점
            textViewBestReviewCommentNum.text = review[1].commentNum.toString() // 댓글 개수
            textViewBestReviewLikeNum.text = review[1].likeNum.toString() // 좋아요 개수
            textViewBestReviewCommentNum.text = review[1].commentNum.toString() // 댓글 개수
            textViewBestReviewViewNum.text = review[1].viewNum.toString() // 조회수

            if (review[1].isLike) { // 좋아요 했으면
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            // 편의점 브랜드 로고 설정
            val csBrandStrokeColor = MyApplication.getCsBrandColor(review[1].csBrand)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(review[1].csBrand)

            if (csBrandResourceId == -1 &&  csBrandStrokeColor == -1) { // 편의점 브랜드가 기타이면
                imageViewBestReviewCsBrand.visibility = View.INVISIBLE
                textViewBestReviewCsBrandEtc.visibility = View.INVISIBLE
            } else {
                // 편의점 브랜드 설정
                cardViewBestReviewCsBrandContainer.strokeColor = csBrandStrokeColor
                imageViewBestReviewCsBrand.setImageResource(csBrandResourceId)
            }
        }

        with(binding.layoutBestReviewRootContainer3) {
            Glide.with(parent.context)
                .load(review[2].itemImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.ic_all_404)
                .fallback(R.drawable.ic_all_404)
                .into(imageViewBestReviewItemImage)

            textViewBestReviewTitle.text = review[2].itemName // 아이템 이름
            textViewBestReviewPrice.text = review[2].itemPrice // 가격
            textViewBestReviewRating.text = review[2].itemStarScore.toString() // 별점
            textViewBestReviewCommentNum.text = review[2].commentNum.toString() // 댓글 개수
            textViewBestReviewLikeNum.text = review[2].likeNum.toString() // 좋아요 개수
            textViewBestReviewCommentNum.text = review[2].commentNum.toString() // 댓글 개수
            textViewBestReviewViewNum.text = review[2].viewNum.toString() // 조회수

            if (review[2].isLike) { // 좋아요 했으면
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewBestReviewLikeNumImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

            // 편의점 브랜드 로고 설정
            val csBrandStrokeColor = MyApplication.getCsBrandColor(review[2].csBrand)
            val csBrandResourceId = MyApplication.getCsBrandResourceId(review[2].csBrand)

            if (csBrandResourceId == -1 &&  csBrandStrokeColor == -1) { // 편의점 브랜드가 기타이면
                imageViewBestReviewCsBrand.visibility = View.INVISIBLE
                textViewBestReviewCsBrandEtc.visibility = View.INVISIBLE
            } else {
                // 편의점 브랜드 설정
                cardViewBestReviewCsBrandContainer.strokeColor = csBrandStrokeColor
                imageViewBestReviewCsBrand.setImageResource(csBrandResourceId)
            }
        }
    }

}