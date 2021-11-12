package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.home.event_item.domain.GetEventItemsRes
import com.mju.csmoa.home.more.model.GetUserInfoRes
import com.mju.csmoa.home.more.model.PatchUserInfoReq
import com.mju.csmoa.login.domain.model.GetRefreshJwtTokenRes
import com.mju.csmoa.login.domain.model.PostLoginReq
import com.mju.csmoa.login.domain.model.PostOAuthLoginReq
import com.mju.csmoa.login.domain.model.PostSignUpReq
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

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
    fun refreshJwtToken(@Header("Refresh-Token") refreshToken: String): GetRefreshJwtTokenRes

    // 이벤트 아이템 메인
    @GET("/event-items")
    fun getEventItems(): Call<JsonElement>

    @GET("/event-items")
    suspend fun getEventItemsTemp(@Query("page") pageNum: Int): GetEventItemsRes

    // 특정 이벤트 아이템 클릭
    @GET("/event-items/{eventItemId}")
    fun getEventItem(@Path("eventItemId") eventItemId: Long): Call<JsonElement>

    // 사용자 정보 받아오기
    @GET("/user-info")
    suspend fun getUserInfo(@Header("Access-Token") accessToken: String): GetUserInfoRes

    // 사용자 정보 수정 (수정된 정보 return)
    @Multipart
    @PATCH("/user-info")
    suspend fun patchUserInfo(
        @Header("Access-Token") accessToken: String,
        @Body patchUserInfoReq: PatchUserInfoReq
    ): GetUserInfoRes

}
