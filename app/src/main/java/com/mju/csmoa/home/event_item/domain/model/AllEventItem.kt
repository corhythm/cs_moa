package com.mju.csmoa.home.event_item.domain.model

data class AllEventItem(
    val type: Int,
    val recommendedEventItemList: ArrayList<ItemEventItem>?,
    val eventItemList: ArrayList<ItemEventItem>?
) {
    companion object {
        const val RECOMMENDED_TYPE = 0 // 추천 아이템
        const val EVENT_ITEM_TYPE = 1 // 행사 상품
    }
}
