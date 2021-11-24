package com.mju.csmoa.home.review.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.review.domain.model.Comment
import kotlinx.coroutines.flow.Flow

class PagingCommentViewModel : ViewModel() {
    private var reviewId: Long? = null

    fun setReviewId(reviewId: Long) { this.reviewId = reviewId}

    fun getComments(): Flow<PagingData<Comment>> {
        return Pager(config = PagingConfig(pageSize = CommentPagingDataSource.PAGE_SIZE),
            pagingSourceFactory = { CommentPagingDataSource(reviewId ?: 1) }).flow.cachedIn(viewModelScope)
    }
}