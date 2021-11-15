package com.mju.csmoa.home.event_item.domain

import com.mju.csmoa.home.event_item.domain.model.EventItem

data class GetDetailEventItemRes(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: DetailEventItem
)

data class DetailEventItem(val detailEventItem: EventItem, val detailRecommendedEventItems: List<EventItem>)
