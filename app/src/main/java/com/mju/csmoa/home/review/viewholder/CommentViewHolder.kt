package com.mju.csmoa.home.review.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemCommentBinding
import com.mju.csmoa.home.review.domain.model.Comment

class CommentViewHolder(
    private val parent: ViewGroup,
    private val onNestedCommentClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ).root
    ) {

    private val binding = ItemCommentBinding.bind(itemView)

    fun bind(comment: Comment) {
        with(binding) {
            Glide.with(parent.context)
                .load(comment.userProfileImageUrl)
                .placeholder(R.drawable.ic_all_loading)
                .error(R.drawable.img_all_basic_profile)
                .fallback(R.drawable.img_all_basic_profile)
                .into(imageViewCommentProfileImage)

            textViewCommentNickname.text = comment.nickname;
            textViewCommentCreatedAt.text = comment.createdAt
            textViewCommentContent.text = comment.commentContent

            // 답글이 있으면
            if (comment.nestedCommentNum > 0) {
                cardViewCommentGoToNestedComment.visibility = View.VISIBLE
                textViewCommentNestedCommentNum.text = "답글(${comment.nestedCommentNum})"

                // 답글 클릭하면
                cardViewCommentGoToNestedComment.setOnClickListener { onNestedCommentClicked.invoke(absoluteAdapterPosition) }
            }
        }
    }
}