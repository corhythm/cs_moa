package com.mju.csmoa.home.review.domain.model

data class Comment(
    val reviewCommentId: Long,
    val userId: Long,
    val nickname: String,
    val userProfileImageUrl: String,
    val bundleId: Long,
    val commentContent: String,
    val nestedCommentNum: Int,
    val createdAt: String
)
