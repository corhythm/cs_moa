package com.mju.csmoa.main.search

// 최근 검색어 저장할 떄, 어떤 항목에서 검색했는지 구분(e.g. 제품리뷰에서 검색 -> 0, 행사상품에서 검색 -> 1)
enum class SearchHistoryType {
    REVIEW, EVENT, RECIPE
}