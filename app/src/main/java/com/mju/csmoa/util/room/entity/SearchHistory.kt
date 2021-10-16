package com.mju.csmoa.util.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// 어디서 검색한 검색어인지 e.g. 제품리뷰(0), 행사상품(1), 레시피(?)
@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var searchHistoryId: Int = 0,
    val searchWord: String, val createdAt: String, val type: Int
)