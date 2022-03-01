package com.mju.csmoa.home.review.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mju.csmoa.JwtTokenInfo
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants
import com.mju.csmoa.common.util.MyApplication


class MyReviewPagingDataSource(private val whenLoadingFinished: () -> Unit) : PagingSource<Int, Review>() {

    private lateinit var jwtTokenInfo: JwtTokenInfo

    companion object {
        private const val FIRST_PAGE_INDEX = 1
        const val PAGE_SIZE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            // 네트워크 자원 조금 손해보더라도 여기서 받아와야 lateinit null exception 안 걸림
            jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!

            // accessToken 만료되면 다시 받아오기
            if (MyApplication.instance.jwtService.isAccessTokenExpired(jwtTokenInfo.accessToken)) {
                jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!
            }

            val position = params.key ?: FIRST_PAGE_INDEX
            val response =
                RetrofitManager.retrofitService?.getMyReviews(jwtTokenInfo.accessToken, position)

            val reviews = response?.result!!

            if (position == 1) {
                whenLoadingFinished()
            }

            LoadResult.Page(
                data = reviews,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (reviews.size < PAGE_SIZE) null else position.plus(1)
            )
        } catch (ex: Exception) {
            Log.d(
                Constants.TAG,
                "ReviewPagingSource -load() called (error!!!!!) / ${ex.printStackTrace()}"
            )
            Log.d(Constants.TAG, "ReviewPagingSource -load() called (error) / ${ex.message}")
            LoadResult.Error(ex)
        }
    }
}