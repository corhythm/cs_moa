package com.mju.csmoa.home.review.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val reviewId: Long,
    val userId: Long,
    val itemName: String,
    val itemPrice: String,
    val itemStarScore: Float,
    val itemImageUrl: String,
    val csBrand: String,
    val content: String,
    val likeNum: Int,
    val commentNum: Int,
    val viewNum: Int,
    val createdAt: String,
    val isLike: Boolean
) : Parcelable
