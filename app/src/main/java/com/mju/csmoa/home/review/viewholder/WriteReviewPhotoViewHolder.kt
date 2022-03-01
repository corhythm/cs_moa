package com.mju.csmoa.home.review.viewholder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.databinding.ItemWriteReviewCameraBinding
import com.mju.csmoa.databinding.ItemWriteReviewPhotoBinding
import com.mju.csmoa.home.review.domain.model.Photo
import com.mju.csmoa.common.util.Constants.TAG


class WriteReviewCameraViewHolder(parent: ViewGroup, onCameraClicked: () -> Unit) :
    RecyclerView.ViewHolder(
        ItemWriteReviewCameraBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {
    private val binding = ItemWriteReviewCameraBinding.bind(itemView)

    init {
        // 루트 뷰홀더 클릭되면 -> 카메라 또는 갤러리에서 리뷰에 사용할 이미지 가져오기
        binding.root.setOnClickListener { onCameraClicked.invoke() }
    }

    fun bind(pictureNum: Int) { // 현재 추가된 사진 이미지 개수 넘겨주기
        binding.textViewWriteReviewCameraImageCount.text = "$pictureNum/5"
    }

}

class WriteReviewPhotoViewHolder(
    private val parent: ViewGroup,
    onCancelClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemWriteReviewPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).root
    ) {

    private val binding = ItemWriteReviewPhotoBinding.bind(itemView)

    init {
        // 추가된 사진 취소 버튼 클릭 -> 리스트에서 삭제
        binding.cardViewWriteReviewPhotoCancel.setOnClickListener {
            binding.cardViewWriteReviewPhotoCancel.strokeWidth = 0
            Log.d(TAG, "삭제되는 뷰홀더 position: $absoluteAdapterPosition")
            onCancelClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(photo: Photo) {
        Log.d(TAG, "WriteReviewPictureViewHolder -bind() called / reviewPicture = $photo")
//        binding.imageViewWriteReviewImageImage.setImageURI(reviewPicture.pictureUri)
        Glide.with(parent.context)
            .load(photo.pictureUri)
            .into(binding.imageViewWriteReviewPhotoImage)
    }

}
