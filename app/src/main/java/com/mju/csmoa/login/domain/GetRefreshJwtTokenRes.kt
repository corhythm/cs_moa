package com.mju.csmoa.login.domain

data class GetRefreshJwtTokenRes(val isSuccess: String, val code: Int, val message: String, val jwtToken: JwtToken)

data class JwtToken(val userId: Long, val accessToken: String, val refreshToken: String)
