package com.mju.csmoa.home.review.domain

data class Review(
    val reviewId: Long,
    val userId: Long,
    val itemName: String,
    val itemPrice: String,
    val itemStarScore: Float,
    val imageUrls: List<String>,
    val csBrand: String,
    val itemImgSrc: String,
    val heartNum: Int,
    val commentNum: Int,
    val createdAt: String,
)
