package com.mju.csmoa.home.event_item.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.retrofit.RetrofitManager

class EventItemPagingSource : PagingSource<Int, EventItem>() {

    companion object {
        private const val FIRST_PAGE_INDEX = 1
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
            val position = params.key ?: FIRST_PAGE_INDEX
            val response = RetrofitManager.retrofitService?.getEventItemsTemp(position)

            /* 로드에 성공 시 LoadResult.Page 반환
            data : 전송되는 데이터
            prevKey : 이전 값 (위 스크롤 방향)
            nextKey : 다음 값 (아래 스크롤 방향)
            */
            LoadResult.Page(
                data = response?.result?.eventItemList!!,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = position.plus(1)
            )

        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }
}