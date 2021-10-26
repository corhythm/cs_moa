package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.model.PostOAuthLogin
import com.mju.csmoa.login.domain.model.PostSignUpReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IRetrofit {

    @POST("/signup")
    @Headers("accept: application/json", "content-type: application/json")
    fun signUp(@Body postSignUpReq: PostSignUpReq): Call<JsonElement>

    @POST("/login")
    fun login(): Call<JsonElement>

    @POST("/login/oauth")
    fun kakaoLogin(@Body postOAuthLogin: PostOAuthLogin): Call<JsonElement>

}
