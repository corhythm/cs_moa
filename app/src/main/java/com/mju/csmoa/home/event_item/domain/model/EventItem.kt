package com.mju.csmoa.home.event_item.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EventItem(
    val eventItemId: Long?,
    val itemName: String?,
    var itemPrice: String?,
    var itemActualPrice: String?,
    val itemImageSrc: String?,
    val itemCategory: String?,
    val csBrand: String?,
    val itemEventType: String?,
    val viewCount: Int?,
    val likeCount: Int?,
    var colorCode: String?,
    var isLike: Boolean?
) : Parcelable


