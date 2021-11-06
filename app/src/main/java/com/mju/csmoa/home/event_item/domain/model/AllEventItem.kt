package com.mju.csmoa.home.event_item.domain.model

data class AllEventItem(
    val type: Int,
    val recommendedEventItemList: ArrayList<ItemEventItem>?,
//    val eventItemList: ArrayList<ItemEventItem>?
    val eventItem: ItemEventItem?
)
