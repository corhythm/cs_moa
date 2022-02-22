package com.mju.csmoa.home.event_item.domain.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventItemViewModel(recommendEventItems: List<EventItem>, normalEventItems: List<EventItem>) : ViewModel() {
    private val _recommendedEventItems = MutableLiveData<List<EventItem>>().apply { value =  recommendEventItems }
    val recommendedEventItems: LiveData<List<EventItem>> get() = _recommendedEventItems
    private val _normalEventItems = MutableLiveData<List<EventItem>>().apply { value = normalEventItems }
    val normalEventItems: LiveData<List<EventItem>> get() = _normalEventItems
}