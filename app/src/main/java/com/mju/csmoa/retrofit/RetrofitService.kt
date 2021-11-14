package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.home.event_item.domain.GetEventItemsRes
import com.mju.csmoa.home.more.model.GetUserInfoRes
import com.mju.csmoa.home.more.model.PatchUserInfoRes
import com.mju.csmoa.login.domain.model.GetRefreshJwtTokenRes
import com.mju.csmoa.login.domain.model.PostLoginReq
import com.mju.csmoa.login.domain.model.PostOAuthLoginReq
import com.mju.csmoa.login.domain.model.PostSignUpReq
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    // NOTE: 회원가입
    @POST("/signup")
    @Headers("accept: application/json", "content-type: application/json")
    fun signUp(@Body postSignUpReq: PostSignUpReq): Call<JsonElement>

    // NOTE: 로그인
    @POST("/login")
    fun login(@Body postLoginReq: PostLoginReq): Call<JsonElement>

    // NOTE: OAuth 로그인
    @POST("/login/oauth")
    fun oAuthLogin(@Body postOAuthLoginReq: PostOAuthLoginReq): Call<JsonElement>

    // NOTE: 사용자 정보 받아오기
    @GET("/user-info")
    suspend fun getUserInfo(@Header("Access-Token") accessToken: String): GetUserInfoRes

    // NOTE: 사용자 프로필 정보 변경(프로필 이미지, 닉네임)
    @Multipart
    @PATCH("/user-info")
    suspend fun patchUserInfo(
        @Header("Access-Token") accessToken: String, @Part profileImageFile: MultipartBody.Part?,
        @Part("nickname") nickname: RequestBody?
    ): PatchUserInfoRes?

    // NOTE: JWT 토큰 갱신
    @GET("/token")
    fun refreshJwtToken(@Header("Refresh-Token") refreshToken: String): GetRefreshJwtTokenRes

    // NOTE: 추천 행사 상품 가져오기
    @GET("/recommended-event-items")
    suspend fun getRecommendedEventItems(@Header("Access-Token") accessToken: String): GetEventItemsRes

    // NOTE: 일반 행사 상품 가져오기
    @GET("/event-items")
    suspend fun getEventItems(
        @Header("Access-Token") accessToken: String,
        @Query("page") pageNum: Int
    ): GetEventItemsRes

    // NOTE: 특정 행사 상품에 대한 추천 행사 상품 가져오기
    @GET("/event-items/{eventItemId}")
    suspend fun getDetailRecommendedEventItems(
        @Header("Access-Token") accessToken: String,
        @Path("eventItemId") eventItemId: Long
    ): GetEventItemsRes


}
