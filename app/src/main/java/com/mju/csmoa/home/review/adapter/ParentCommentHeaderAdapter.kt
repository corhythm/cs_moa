package com.mju.csmoa.home.review.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.viewholder.ParentCommentHeaderViewHolder

class ParentCommentHeaderAdapter(private val parentComment: Comment) :
    RecyclerView.Adapter<ParentCommentHeaderViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ParentCommentHeaderViewHolder(parent)


    override fun onBindViewHolder(holder: ParentCommentHeaderViewHolder, position: Int) {
        holder.bind(parentComment)
    }

    override fun getItemCount() = 1
}