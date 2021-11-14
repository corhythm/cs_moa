package com.mju.csmoa.home.event_item.paging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.event_item.domain.model.EventItem
import kotlinx.coroutines.flow.Flow

class EventItemViewModel : ViewModel() {

    fun getEventItems(): Flow<PagingData<EventItem>> {
        return Pager(config = PagingConfig(pageSize = 14),
            pagingSourceFactory = { EventItemPagingSource() }).flow.cachedIn(viewModelScope)
    }
}