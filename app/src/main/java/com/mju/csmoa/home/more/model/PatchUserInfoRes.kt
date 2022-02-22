package com.mju.csmoa.home.more.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 임시 객체는 못 만드나
@Parcelize
data class PatchUserInfoRes(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: UpdatedUserProfileInfo
) : Parcelable

@Parcelize
data class UpdatedUserProfileInfo(
    val userId: Long,
    val nickname: String,
    val userProfileImageUrl: String
) : Parcelable


