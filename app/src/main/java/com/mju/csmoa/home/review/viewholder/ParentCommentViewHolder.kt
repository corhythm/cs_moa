package com.mju.csmoa.home.review.viewholder

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ItemCommentBinding
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.common.util.Constants.TAG

class ParentCommentViewHolder(
    private val parent: ViewGroup,
    private val onChildCommentClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(
        ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ).root
    ) {

    private val binding = ItemCommentBinding.bind(itemView)

    fun bind(comment: Comment) {
        Log.d(TAG, "ParentCommentViewHolder -bind() called / position = $absoluteAdapterPosition")

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
                textViewCommentNestedCommentNum.text = "답글(${comment.nestedCommentNum})"
                cardViewCommentGoToNestedComment.setCardBackgroundColor(Color.parseColor("#eee3e7"))
            } else { // 답글이 없으면
                textViewCommentNestedCommentNum.text = "답글쓰기"
                cardViewCommentGoToNestedComment.setCardBackgroundColor(Color.parseColor("#FF8C94"))
            }

            // 답글 클릭하면
            cardViewCommentGoToNestedComment.setOnClickListener { onChildCommentClicked.invoke(absoluteAdapterPosition) }
        }
    }
}