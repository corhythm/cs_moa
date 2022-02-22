package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemHeaderParentCommentBinding
import com.mju.csmoa.home.review.domain.model.Comment

class ParentCommentHeaderViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemHeaderParentCommentBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    ).root
) {
    private val binding = ItemHeaderParentCommentBinding.bind(itemView)

    fun bind(parentComment: Comment) {
        with(binding) {
            Glide.with(parent.context)
                .load(parentComment.userProfileImageUrl)
                .placeholder(R.drawable.img_all_basic_profile)
                .error(R.drawable.img_all_basic_profile)
                .fallback(R.drawable.img_all_basic_profile)
                .into(imageViewParentCommentProfileImage)

            textViewParentCommentNickname.text = parentComment.nickname;
            textViewParentCommentCreatedAt.text = parentComment.createdAt
            textViewParentCommentContent.text = parentComment.commentContent
            textViewParentCommentCommentNum.text = parentComment.nestedCommentNum.toString()
        }
    }
}