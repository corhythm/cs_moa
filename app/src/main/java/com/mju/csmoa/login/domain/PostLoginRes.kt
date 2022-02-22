package com.mju.csmoa.login.domain

data class PostLoginRes(val userId: Long, val accessToken: String, val refreshToken: String)
