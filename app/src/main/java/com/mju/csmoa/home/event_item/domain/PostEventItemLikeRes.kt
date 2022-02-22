package com.mju.csmoa.home.event_item.domain

data class PostEventItemLikeRes(
    val userId: Long,
    val eventItemId: Long,
    val isLike: Boolean
)

