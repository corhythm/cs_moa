package com.mju.csmoa.home.event_item.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mju.csmoa.JwtTokenInfo
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication

class EventItemPagingSource(
    private val csBrands: MutableList<String>,
    private val eventTypes: MutableList<String>,
    private val categories: MutableList<String>,
) : PagingSource<Int, EventItem>() {

    private lateinit var jwtTokenInfo: JwtTokenInfo

    companion object {
        private const val FIRST_PAGE_INDEX = 1
        const val PAGE_SIZE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, EventItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventItem> {
        // LoadParams : 로드할 키와 항목 수 , LoadResult : 로드 작업의 결과
        return try {

            // 네트워크 자원 조금 손해보더라도 여기서 받아와야 lateinit null exception 안 걸림
            jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!

            // accessToken 만료되면 다시 받아오기
            if (MyApplication.instance.jwtService.isAccessTokenExpired(jwtTokenInfo.accessToken)) {
                jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()!!
            }

            val position = params.key ?: FIRST_PAGE_INDEX
            val response =
                RetrofitManager.retrofitService?.getEventItems(
                    jwtTokenInfo.accessToken,
                    position,
                    csBrands,
                    eventTypes,
                    categories
                )

            /* 로드에 성공 시 LoadResult.Page 반환
            data : 전송되는 데이터
            prevKey : 이전 값 (위 스크롤 방향)
            nextKey : 다음 값 (아래 스크롤 방향)
            */

            LoadResult.Page(
                data = response?.result!!,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (response.result.size < PAGE_SIZE) null else position.plus(1)
            )

        } catch (ex: Exception) {
            Log.d(
                TAG,
                "EventItemPagingSource -load() called (error!!!!!) / ${ex.printStackTrace()}"
            )
            Log.d(TAG, "EventItemPagingSource -load() called (error) / ${ex.message}")
            LoadResult.Error(ex)
        }
    }
}