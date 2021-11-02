package com.mju.csmoa.login.domain.model

data class GetJwtTokenRes(val userId: Long, val accessToken: String, val refreshToken: String)
