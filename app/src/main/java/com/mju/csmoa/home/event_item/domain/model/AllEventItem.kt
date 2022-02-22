package com.mju.csmoa.home.event_item.domain.model

data class AllEventItem(
    val type: Int,
    val recommendedEventItemList: List<EventItem>?,
    val eventItemList: List<EventItem>?
)
