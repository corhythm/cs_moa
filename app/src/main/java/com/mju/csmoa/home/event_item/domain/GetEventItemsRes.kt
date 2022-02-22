package com.mju.csmoa.home.event_item.domain;

import com.mju.csmoa.home.event_item.domain.model.EventItem

data class GetEventItemsRes(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<EventItem>
)
