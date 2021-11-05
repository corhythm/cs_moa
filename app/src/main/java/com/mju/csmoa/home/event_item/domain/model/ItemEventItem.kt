package com.mju.csmoa.home.event_item.domain.model

import android.os.Parcel
import android.os.Parcelable

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
    val likeCount: Int = 200,
    var colorCode: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(eventItemId)
        parcel.writeString(itemName)
        parcel.writeString(itemPrice)
        parcel.writeString(itemActualPrice)
        parcel.writeString(itemImageSrc)
        parcel.writeString(itemCategory)
        parcel.writeString(csBrand)
        parcel.writeString(itemEventType)
        parcel.writeInt(viewCount)
        parcel.writeInt(likeCount)
        parcel.writeString(colorCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemEventItem> {
        override fun createFromParcel(parcel: Parcel): ItemEventItem {
            return ItemEventItem(parcel)
        }

        override fun newArray(size: Int): Array<ItemEventItem?> {
            return arrayOfNulls(size)
        }
    }
}