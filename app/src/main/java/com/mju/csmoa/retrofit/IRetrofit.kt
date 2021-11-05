package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.model.PostLoginReq
import com.mju.csmoa.login.domain.model.PostOAuthLoginReq
import com.mju.csmoa.login.domain.model.PostSignUpReq
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {

    // 회원가입
    @POST("/signup")
    @Headers("accept: application/json", "content-type: application/json")
    fun signUp(@Body postSignUpReq: PostSignUpReq): Call<JsonElement>

    // 로그인
    @POST("/login")
    fun login(@Body postLoginReq: PostLoginReq): Call<JsonElement>

    // OAuth 로그인
    @POST("/login/oauth")
    fun oAuthLogin(@Body postOAuthLoginReq: PostOAuthLoginReq): Call<JsonElement>

    // JWT 토큰 갱신
    @GET("/token")
    fun refreshJwtToken(@Header("refreshToken") refreshToken: String): Call<JsonElement>

    // 이벤트 아이템 메인
    @GET("/event-items")
    fun getEventItems(): Call<JsonElement>

    // 특정 이벤트 아이템 클릭
    @GET("/event-items/{eventItemId}")
    fun getEventItem(@Path("eventItemId") eventItemId: Long): Call<JsonElement>

}
