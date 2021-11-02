package com.mju.csmoa.home.event_item.domain;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mju.csmoa.home.event_item.domain.model.ItemEventItem

data class GetEventItemsRes(

    @SerializedName("recommendedEventItemList")
    @Expose
    val recommendedEventItemList: List<ItemEventItem>,

    @SerializedName("eventItemList")
    @Expose
    val eventItemList: List<ItemEventItem>
)
