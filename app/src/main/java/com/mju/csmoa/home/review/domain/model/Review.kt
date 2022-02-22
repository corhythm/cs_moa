package com.mju.csmoa.home.review.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val reviewId: Long,
    val userId: Long,
    val reviewName: String,
    val price: String,
    val starScore: Float,
    val reviewImageUrls: List<String>,
    val csBrand: String,
    val content: String,
    var likeNum: Int,
    var commentNum: Int,
    var viewNum: Int,
    val createdAt: String,
    var isLike: Boolean
) : Parcelable
