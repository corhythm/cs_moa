package com.mju.csmoa.home.event_item.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat


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
    var colorCode: String?
) : Parcelable {

    init {
        val decimalFormat = DecimalFormat("#,###")

        if (itemPrice != null && itemActualPrice != null) {
            itemPrice = "${decimalFormat.format(itemPrice!!.toInt())}원"
            itemActualPrice = "(개당 ${decimalFormat.format(itemActualPrice!!.toInt())}원)"
        }
    }
}


