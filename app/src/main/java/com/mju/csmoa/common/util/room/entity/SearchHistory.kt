package com.mju.csmoa.common.util.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// 어디서 검색한 검색어인지 e.g. 제품리뷰(0), 행사상품(1), 레시피(?)
@Entity
data class SearchHistory(
    @PrimaryKey val searchWord: String, val createdAt: String, val type: Short
)