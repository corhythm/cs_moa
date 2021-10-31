package com.mju.csmoa.login.domain.model

data class PostLoginRes(val userId: Long, val accessToken: String, val refreshToken: String)
