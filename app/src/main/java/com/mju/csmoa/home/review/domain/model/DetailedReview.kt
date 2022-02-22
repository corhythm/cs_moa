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
    var likeNum: Int,
    var commentNum: Int,
    val viewNum: Int,
    val createdAt: String,
    var isLike: Boolean
) : Parcelable
