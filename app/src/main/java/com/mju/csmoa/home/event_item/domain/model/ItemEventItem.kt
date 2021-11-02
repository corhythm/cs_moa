package com.mju.csmoa.home.event_item.domain.model

data class ItemEventItem(
    val eventItemId: Long,
    val itemName: String,
    val itemPrice: String,
    val itemActualPrice: String,
    val itemImageSrc: String,
    val itemCategory: String,
    val csBrand: String,
    val itemEventType: String,
    val viewCount: Int = 100,
    val likeCount: Int = 200
)