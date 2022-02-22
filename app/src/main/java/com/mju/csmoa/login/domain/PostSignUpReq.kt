package com.mju.csmoa.login.domain

import com.google.gson.annotations.SerializedName

data class PostSignUpReq(
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("nickname") val nickname: String?,
)