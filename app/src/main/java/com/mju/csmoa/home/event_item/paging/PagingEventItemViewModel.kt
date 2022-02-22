package com.mju.csmoa.home.event_item.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.event_item.domain.model.EventItem
import com.mju.csmoa.home.event_item.paging.EventItemPagingSource.Companion.PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class PagingEventItemViewModel : ViewModel() {

    private lateinit var csBrands: MutableList<String>
    private lateinit var eventTypes: MutableList<String>
    private lateinit var categories: MutableList<String>

    fun setFilterDataList(
        csBrands: MutableList<String>,
        eventTypes: MutableList<String>,
        categories: MutableList<String>
    ) {
        this.csBrands = csBrands
        this.eventTypes = eventTypes
        this.categories = categories
    }

    fun getEventItems(): Flow<PagingData<EventItem>> {
        return Pager(config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                EventItemPagingSource(
                    csBrands,
                    eventTypes,
                    categories
                )
            }).flow.cachedIn(viewModelScope)
    }
}