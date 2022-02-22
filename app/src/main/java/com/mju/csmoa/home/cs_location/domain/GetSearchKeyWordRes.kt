package com.mju.csmoa.home.cs_location.domain

import com.google.gson.annotations.SerializedName

data class GetSearchKeyWordRes(
    val meta: PlaceMeta,
    val documents: List<Place>
)

data class PlaceMeta(
    @SerializedName("total_count")
    var totalCount: Int,           // 검색어에 검색된 문서 수

    @SerializedName("pageable_count")
    var pageableCount: Int,    // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)

    @SerializedName("is_end")
    var isEnd: Boolean,               // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음

    @SerializedName("same_name")
    var sameName: RegionInfo      // 질의어의 지역 및 키워드 분석 정보
)

data class Place(
    var id: String,                     // 장소 ID

    @SerializedName("place_name")
    var placeName: String,             // 장소명, 업체명

    @SerializedName("category_name")
    var categoryName: String,          // 카테고리 이름

    @SerializedName("category_group_code")
    var categoryGroupCode: String,    // 중요 카테고리만 그룹핑한 카테고리 그룹 코드

    @SerializedName("category_group_name")
    var categoryGroupName: String,    // 중요 카테고리만 그룹핑한 카테고리 그룹명
    var phone: String,                  // 전화번호

    @SerializedName("address_name")
    var addressName: String,           // 전체 지번 주소

    @SerializedName("road_address_name")
    var roadAddressName: String,      // 전체 도로명 주소

    var x: String,                      // X 좌표값 혹은 longitude
    var y: String,                      // Y 좌표값 혹은 latitude

    @SerializedName("place_url") var placeUrl: String,              // 장소 상세페이지 URL
    var distance: String                 // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter
)

data class RegionInfo(
    var region: List<String>,           // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    var keyword: String,                // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'

    @SerializedName("selected_region")
    var selectedRegion: String         // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
)


