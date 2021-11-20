package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.ReviewPicture
import com.mju.csmoa.home.review.viewholder.WriteReviewCameraViewHolder
import com.mju.csmoa.home.review.viewholder.WriteReviewPictureViewHolder


class WriteReviewPictureAdapter(
    private val reviewPictures: List<ReviewPicture>,
    private val onCameraClicked: () -> Unit, // 카메라 클릭 됐을 때
    private val onCancelClicked: (position: Int) -> Unit // 추가된 사진 취소 버튼 클릭됐을 때
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CAMERA = 0
        const val PICTURE = 1
    }

    override fun getItemViewType(position: Int) = reviewPictures[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CAMERA) {
            WriteReviewCameraViewHolder(parent, onCameraClicked)
        } else {
            WriteReviewPictureViewHolder(parent, onCancelClicked)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WriteReviewPictureViewHolder) {
            holder.bind(reviewPictures[position])
        } else {
            (holder as WriteReviewCameraViewHolder).bind(reviewPictures.size - 1) // 현재 추가된 사진 이미지 개수 넘겨주기
        }
    }

    override fun getItemCount() = reviewPictures.size
}