package com.mju.csmoa.login.domain.model;

import com.google.gson.annotations.SerializedName

data class PostOAuthLogin (
    @SerializedName("email") val email: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("provider") val provider: String?
)
