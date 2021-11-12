package com.mju.csmoa.home.more.model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

data class PatchUserInfoReq(
    @Part("nickname") val nickname: RequestBody?, //type을 String으로 해도 자동으로 타입변경 해주지만 사이드 이펙트 가능성 있음.
    @Part val file: MultipartBody.Part?
)