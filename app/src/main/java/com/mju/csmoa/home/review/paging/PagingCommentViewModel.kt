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
    private var id: Long? = null
    private var depth: Int? = null

    fun setCommentType(depth: Int, id: Long) {
        this.depth = depth
        this.id = id
    }

    fun getComments(): Flow<PagingData<Comment>> {
        return Pager(config = PagingConfig(pageSize = CommentPagingDataSource.PAGE_SIZE),
            pagingSourceFactory = {
                CommentPagingDataSource(
                    depth = depth ?: 1,
                    id = id ?: 1
                )
            }).flow.cachedIn(viewModelScope)
    }
}