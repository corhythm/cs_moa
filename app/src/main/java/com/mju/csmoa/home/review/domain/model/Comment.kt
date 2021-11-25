package com.mju.csmoa.home.review.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Comment(
    val reviewCommentId: Long,
    val userId: Long,
    val nickname: String,
    val userProfileImageUrl: String,
    val bundleId: Long,
    val commentContent: String,
    val nestedCommentNum: Int,
    val createdAt: String,
    val depth: Int
) : Parcelable
