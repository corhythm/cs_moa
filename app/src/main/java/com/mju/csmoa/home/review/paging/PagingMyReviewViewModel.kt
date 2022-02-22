package com.mju.csmoa.home.review.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.review.domain.model.Review
import kotlinx.coroutines.flow.Flow


class PagingMyReviewViewModel : ViewModel() {

    // 이런 코드는 너무 더럽다....
    private lateinit var whenLoadingFinished: () -> Unit

    fun setWhenLoadingFinished(whenLoadingFinished: () -> Unit) {
        this.whenLoadingFinished = whenLoadingFinished
    }

    fun getMyReviews(): Flow<PagingData<Review>> {
        return Pager(config = PagingConfig(pageSize = ReviewPagingDataSource.PAGE_SIZE),
            pagingSourceFactory = { MyReviewPagingDataSource(whenLoadingFinished) }).flow.cachedIn(viewModelScope)
    }
}