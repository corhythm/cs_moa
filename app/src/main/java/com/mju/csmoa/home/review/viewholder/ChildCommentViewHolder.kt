package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemChildCommentBinding
import com.mju.csmoa.home.review.domain.model.Comment

class ChildCommentViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemChildCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {
    private val binding = ItemChildCommentBinding.bind(itemView)

    fun bind(comment: Comment) {
        with(binding) {
            Glide.with(parent.context)
                .load(comment.userProfileImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.img_all_basic_profile)
                .fallback(R.drawable.img_all_basic_profile)
                .into(imageViewChildCommentProfileImage)

            textViewChildCommentNickname.text = comment.nickname;
            textViewChildCommentCreatedAt.text = comment.createdAt
            textViewChildCommentContent.text = comment.commentContent

        }
    }
}