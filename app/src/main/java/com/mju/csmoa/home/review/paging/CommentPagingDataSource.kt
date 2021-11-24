package com.mju.csmoa.home.review.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants

class CommentPagingDataSource(private val reviewId: Long) : PagingSource<Int, Comment>() {

    companion object {
        private const val FIRST_PAGE_INDEX = 1
        const val PAGE_SIZE = 5
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        return try {
            val position = params.key ?: FIRST_PAGE_INDEX
            val response = RetrofitManager.retrofitService?.getReviewComments(reviewId, position)
            val comments = response?.result!!

            LoadResult.Page(
                data = comments,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (comments.size < PAGE_SIZE) null else position.plus(1)
            )
        } catch (ex: Exception) {
            Log.d(
                Constants.TAG,
                "CommentPagingSource -load() called (error!!!!!) / ${ex.printStackTrace()}"
            )
            Log.d(Constants.TAG, "CommentPagingSource -load() called (error) / ${ex.message}")
            LoadResult.Error(ex)
        }
    }


}