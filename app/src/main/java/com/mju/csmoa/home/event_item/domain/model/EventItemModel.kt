package com.mju.csmoa.home.event_item.domain.model

sealed class EventItemModel {
    class RecommendedEventItemModel(val recommendedEventItem: EventItem) : EventItemModel()
    class NormalEventItemModel(val normalEventItem: EventItem) : EventItemModel()
}