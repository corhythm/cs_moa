package com.mju.csmoa.home.review.domain

data class PostReviewLikeRes(
    val reviewId: Long,
    val userId: Long,
    val isLike: Boolean
)
