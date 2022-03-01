package com.mju.csmoa.home.review.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants

class CommentPagingDataSource(private val depth: Int, private val id: Long) :
    PagingSource<Int, Comment>() {

    companion object {
        const val FIRST_PAGE_INDEX = 1
        const val PAGE_SIZE = 5
        const val PARENT_COMMENT = 1 // 부모 댓글이면 id는 reviewId
        const val CHILD_COMMENT = 0 // 자식 댓글이면 id는 reviewCommentId
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
            val response = if (depth == 1)
                RetrofitManager.retrofitService?.getReviewParentComments(reviewId = id, pageNum = position) // 부모 댓글
            else
                RetrofitManager.retrofitService?.getReviewChildComments(bundleId = id, pageNum = position) // 자식 댓글
            val comments = response?.result!!

            LoadResult.Page(
                data = comments,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (comments.isEmpty()) null else position.plus(1)
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