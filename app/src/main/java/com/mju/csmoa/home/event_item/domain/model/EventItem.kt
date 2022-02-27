package com.mju.csmoa.home.event_item.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EventItem(
        val eventItemId: Long?,
        val itemName: String?,
        var itemPrice: String?,
        var itemActualPrice: String?,
        val itemImageUrl: String?,
        val itemCategory: String?,
        val csBrand: String?,
        val itemEventType: String?,
        var viewCount: Int?,
        var likeCount: Int?,
        var colorCode: String?,
        var isLike: Boolean?
) : Parcelable


