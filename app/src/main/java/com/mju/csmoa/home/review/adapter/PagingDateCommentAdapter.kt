package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.viewholder.CommentViewHolder


class PagingDataCommentAdapter(private val onNestedCommentClicked: (position: Int) -> Unit) :
    PagingDataAdapter<Comment, CommentViewHolder>(CommentDiffUtilCallback()) {

    override fun getItemViewType(position: Int) = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(parent, onNestedCommentClicked)

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}

class CommentDiffUtilCallback : DiffUtil.ItemCallback<Comment>() {

    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.reviewCommentId == newItem.reviewCommentId
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.reviewCommentId == newItem.reviewCommentId &&
                oldItem.commentContent == newItem.commentContent
    }

}