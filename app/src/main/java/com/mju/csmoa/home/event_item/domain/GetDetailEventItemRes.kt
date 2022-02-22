package com.mju.csmoa.home.event_item.domain

import com.mju.csmoa.home.event_item.domain.model.EventItem


data class GetDetailEventItemRes(
    val detailEventItem: EventItem,
    val detailRecommendedEventItems: List<EventItem>
)
