package com.mju.csmoa.home.review.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailedReview(
    val reviewId: Long,
    val userId: Long,
    val userProfileImageUrl: String,
    val nickname: String,
    val itemName: String,
    val itemPrice: String,
    val itemStarScore: Float,
    val itemImageUrls: List<String>,
    val csBrand: String,
    val content: String,
    val likeNum: Int,
    val commentNum: Int,
    val viewNum: Int,
    val createdAt: String,
    val isLike: Boolean
) : Parcelable
