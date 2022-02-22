package com.mju.csmoa.retrofit.common_domain

data class BaseResponse<T>(
    val isSuccess: Boolean,
    val message: String,
    val code: Int,
    var result: T? = null
)
