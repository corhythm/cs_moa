package com.mju.csmoa.home.review.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemDetailedReviewBinding
import com.mju.csmoa.home.review.adapter.DetailedReviewImageAdapter
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.util.MyApplication

class DetailedReviewViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemDetailedReviewBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {

    private val binding = ItemDetailedReviewBinding.bind(itemView)

    fun bind(detailedReview: DetailedReview) {
        with(binding) {
            Glide.with(parent.context)
                .load(detailedReview.userProfileImageUrl)
                .placeholder(R.drawable.img_all_basic_profile)
                .error(R.drawable.img_all_basic_profile)
                .fallback(R.drawable.img_all_basic_profile)
                .into(imageViewDetailedReviewProfileImage)

            textViewDetailedReviewNickname.text = detailedReview.nickname // 닉네임
            textViewDetailedReviewReviewTitle.text = detailedReview.itemName // 리뷰 제목
            textViewDetailedReviewPrice.text = detailedReview.itemPrice // 가격
            textViewDetailedReviewStarScore.text = detailedReview.itemStarScore.toString() // 별점
            textViewDetailedReviewLikeNum.text = detailedReview.likeNum.toString() // 좋아요 개수
            textViewDetailedReviewCommentNum.text = "댓글(${detailedReview.commentNum})" // 댓글 개수
            textViewDetailedReviewViewNum.text = detailedReview.viewNum.toString() // 조회수
            textViewDetailedReviewCreatedAt.text = detailedReview.createdAt // 생성 일자
            textViewDetailedReviewContent.text = detailedReview.content // 리뷰 내용

            val csBrandResourceId = MyApplication.getCsBrandResourceId(detailedReview.csBrand) // cs이미지
            val csBrandColor = MyApplication.getCsBrandColor(detailedReview.csBrand)

            if (csBrandResourceId == -1 && csBrandColor == -1) { // 편의점 브랜드가 기타이면
                imageViewDetailedReviewCsBrand.visibility = View.INVISIBLE
                textViewDetailedReviewCsBrandEtc.visibility = View.VISIBLE
                cardViewDetailedReviewCsBrandContainer.strokeColor = Color.parseColor("#F67280")
                textViewDetailedReviewCsBrandEtc.setTextColor(Color.parseColor("#F67280"))
            } else {
                // 편의점 브랜드 설정
                imageViewDetailedReviewCsBrand.setImageResource(csBrandResourceId)
                cardViewDetailedReviewCsBrandContainer.strokeColor = csBrandColor
            }

            // 뷰페이저 설정
            viewpager2DetailedReviewBestReviews.apply {
                adapter = DetailedReviewImageAdapter(detailedReview.itemImageUrls)
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                dotsIndicatorDetailedReviewIndicator.setViewPager2(this)
            }

            // 좋아요 눌렀는지 체크
            if (detailedReview.isLike) { // 좋아요 했으면
                imageViewDetailedReviewLikeImage.setImageResource(R.drawable.ic_all_filledheart)
            } else {
                imageViewDetailedReviewLikeImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
            }

        }
    }
}