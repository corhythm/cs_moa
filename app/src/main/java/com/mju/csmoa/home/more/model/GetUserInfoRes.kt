package com.mju.csmoa.home.more.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetUserInfoRes(
    val isSuccess: String,
    val code: Int,
    val message: String,
    val result: UserInfo
)

@Parcelize
data class UserInfo(
    val userId: Long?,
    val nickname: String?,
    val email: String?,
    val userProfileImageUrl: String?
) : Parcelable