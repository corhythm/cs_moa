package com.mju.csmoa.home.review.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.review.domain.model.Review
import kotlinx.coroutines.flow.Flow

class PagingReviewViewModel : ViewModel() {

    fun getReviews(): Flow<PagingData<Review>> {
        return Pager(config = PagingConfig(pageSize = ReviewPagingDataSource.PAGE_SIZE),
            pagingSourceFactory = { ReviewPagingDataSource() }).flow.cachedIn(viewModelScope)
    }
}