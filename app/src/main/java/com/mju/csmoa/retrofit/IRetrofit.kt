package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.serializable.PostSignUpReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IRetrofit {

    @POST("/signup")
    @Headers("accept: application/json", "content-type: application/json")
    fun signUp(@Body postSignUpReq: PostSignUpReq): Call<JsonElement>

    @POST("/login")
    @Headers("accept: application/json", "content-type: application/json")
    fun login(): Call<JsonElement>

}
