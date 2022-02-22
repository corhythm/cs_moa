package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.paging.CommentPagingDataSource.Companion.PARENT_COMMENT
import com.mju.csmoa.home.review.viewholder.ParentCommentViewHolder
import com.mju.csmoa.home.review.viewholder.ChildCommentViewHolder


class PagingDataCommentAdapter() :
    PagingDataAdapter<Comment, RecyclerView.ViewHolder>(CommentDiffUtilCallback()) {

    private var onChildCommentClicked: ((Int) -> Unit)? = null

    constructor(onChildCommentClicked: (position: Int) -> Unit) : this() {
        this.onChildCommentClicked = onChildCommentClicked
        }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.depth
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PARENT_COMMENT)
            ParentCommentViewHolder(parent, onChildCommentClicked!!)
        else // CHILD_COMMENT
            ChildCommentViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ParentCommentViewHolder)
            holder.bind(getItem(position)!!)
        else
            (holder as ChildCommentViewHolder).bind(getItem(position)!!)
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