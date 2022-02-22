package com.mju.csmoa.retrofit

import com.google.gson.JsonElement
import com.mju.csmoa.home.cs_location.domain.GetSearchKeyWordRes
import com.mju.csmoa.home.event_item.domain.GetDetailEventItemRes
import com.mju.csmoa.home.event_item.domain.GetEventItemsRes
import com.mju.csmoa.home.event_item.domain.PostEventItemHistoryAndLikeReq
import com.mju.csmoa.home.event_item.domain.PostEventItemLikeRes
import com.mju.csmoa.home.more.model.GetUserInfoRes
import com.mju.csmoa.home.more.model.PatchUserInfoRes
import com.mju.csmoa.home.recipe.domain.PostRecipeLikeRes
import com.mju.csmoa.home.recipe.domain.PostRecipeRes
import com.mju.csmoa.home.recipe.domain.model.DetailedRecipe
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.review.domain.PostReviewLikeRes
import com.mju.csmoa.home.review.domain.PostReviewRes
import com.mju.csmoa.home.review.domain.model.Comment
import com.mju.csmoa.home.review.domain.model.DetailedReview
import com.mju.csmoa.home.review.domain.model.Review
import com.mju.csmoa.login.domain.GetRefreshJwtTokenRes
import com.mju.csmoa.login.domain.PostLoginReq
import com.mju.csmoa.login.domain.PostOAuthLoginReq
import com.mju.csmoa.login.domain.PostSignUpReq
import com.mju.csmoa.retrofit.common_domain.BaseResponse
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
        @Header("Access-Token") accessToken: String,
        @Part profileImageFile: MultipartBody.Part?,
        @Part("nickname") nickname: RequestBody?
    ): PatchUserInfoRes?

    // NOTE: JWT 토큰 갱신
    @GET("/token")
    fun refreshJwtToken(@Header("Refresh-Token") refreshToken: String): GetRefreshJwtTokenRes

    // NOTE: 추천 행사 상품 가져오기
    @GET("/recommended-event-items")
    suspend fun getRecommendedEventItems(
        @Header("Access-Token") accessToken: String,
        @Query("cs-brand") csBrands: List<String>?,
        @Query("event-type") eventTypes: List<String>?,
        @Query("category") categories: List<String>?
    ): GetEventItemsRes

    // NOTE: 일반 행사 상품 가져오기
    @GET("/event-items")
    suspend fun getEventItems(
        @Header("Access-Token") accessToken: String,
        @Query("page") pageNum: Int,
        @Query("cs-brand") csBrands: List<String>?,
        @Query("event-type") eventTypes: List<String>?,
        @Query("category") categories: List<String>?
    ): GetEventItemsRes

    // NOTE: 특정 행사 상품 정보 + 추천 행사 상품 가져오기
    @GET("/event-items/{eventItemId}")
    suspend fun getDetailEventItem(
        @Header("Access-Token") accessToken: String,
        @Path("eventItemId") eventItemId: Long
    ): BaseResponse<GetDetailEventItemRes>

    // NOTE: 행사 상품 조회수 POST
    @POST("/event-items/history")
    suspend fun postEventItemHistory(
        @Header("Access-Token") accessToken: String,
        @Body postEventItemHistoryAndLikeReq: PostEventItemHistoryAndLikeReq
    ): BaseResponse<Boolean>

    // NOTE: 행사 상품 좋아요 POST
    @POST("/event-items/like")
    suspend fun postEventItemLike(
        @Header("Access-Token") accessToken: String,
        @Body postEventItemHistoryAndLikeReq: PostEventItemHistoryAndLikeReq
    ): BaseResponse<PostEventItemLikeRes>

    // NOTE: 리뷰 작성
    @Multipart
    @POST("/reviews")
    suspend fun postReview(
        @Header("Access-Token") accessToken: String,
        @Part reviewImages: List<MultipartBody.Part>,
        @Part("title") title: RequestBody,
        @Part("price") price: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("category") category: RequestBody,
        @Part("csBrand") csBrand: RequestBody,
        @Part("content") content: RequestBody
    ): BaseResponse<PostReviewRes>

    // NOTE: 베스트 리뷰 가져오기
    @GET("/best-reviews")
    suspend fun getBestReviews(
        @Header("Access-Token") accessToken: String
    ): BaseResponse<List<Review>>

    // NOTE: 일반 리뷰 가져오기 & 리뷰 검색
    @GET("/reviews")
    suspend fun getReviews(
        @Header("Access-Token") accessToken: String,
        @Query("search") searchWord: String? = null,
        @Query("page") pageNum: Int
    ): BaseResponse<List<Review>>

    // NOTE: 리뷰 세부 정보 가져오기
    @GET("reviews/{reviewId}")
    suspend fun getDetailedReview(
        @Header("Access-Token") accessToken: String,
        @Path("reviewId") reviewId: Long
    ): BaseResponse<DetailedReview>

    // NOTE: 부모 댓글 가져오기
    @GET("reviews/{reviewId}/comments")
    suspend fun getReviewParentComments(
        @Path("reviewId") reviewId: Long,
        @Query("page") pageNum: Int // 5개씩 가져옴
    ): BaseResponse<List<Comment>>

    // NOTE: 부모 댓글 쓰기
    @POST("reviews/{reviewId}/comments")
    suspend fun postReviewParentComment(
        @Path("reviewId") reviewId: Long,
        @Header("Access-Token") accessToken: String,
        @Body content: RequestBody
    ): BaseResponse<Comment>

    // NOTE: 대댓글 가져오기
    @GET("/comments/{bundleId}/child-comments")
    suspend fun getReviewChildComments(
        @Path("bundleId") bundleId: Long,
        @Query("page") pageNum: Int // 5개씩 가져옴
    ): BaseResponse<List<Comment>>

    // NOTE: 대댓글 쓰기
    @POST("/reviews/{reviewId}/comments/{bundleId}/child-comments")
    suspend fun postReviewChildComment(
        @Path("reviewId") reviewId: Long,
        @Path("bundleId") bundleId: Long,
        @Header("Access-Token") accessToken: String,
        @Body content: RequestBody
    ): BaseResponse<Comment>

    // NOTE: KakaoMap search Query
    @GET("v2/local/search/keyword.json")    // Keyword.json의 정보를 받아옴
    suspend fun getSearchKeyword(
        @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
        @Query("query") query: String,             // 검색을 원하는 질의어 [필수]
        @Query("page") page: Int,
        // 매개변수 추가 가능
        @Query("category_group_code") category: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") rad: Int
    ): GetSearchKeyWordRes

    @POST("/reviews/{reviewId}/like")
    suspend fun postReviewLike(
        @Header("Access-Token") accessToken: String,
        @Path("reviewId") reviewId: Long
    ): BaseResponse<PostReviewLikeRes>

    // NOTE: 레시피 등록
    @Multipart
    @POST("/recipes")
    suspend fun postRecipe(
        @Header("Access-Token") accessToken: String,
        @Part recipeImages: List<MultipartBody.Part>,
        @Part("name") name: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("content") content: RequestBody
    ): BaseResponse<PostRecipeRes>

    // NOTE: 추천 레시피 리스트 가져오기
    @GET("/recommended-recipes")
    suspend fun getRecommendedRecipes(
        @Header("Access-Token") accessToken: String,
    ): BaseResponse<List<Recipe>>

    // NOTE: 일반 레시피 리스트 가져오기
    @GET("/recipes")
    suspend fun getRecipes(
        @Header("Access-Token") accessToken: String,
        @Query("search") searchWord: String? = null,
        @Query("page") pageNum: Int
    ): BaseResponse<List<Recipe>>

    // NOTE: 레시피 세부 정보 가져오기
    @GET("/recipes/{recipeId}")
    suspend fun getDetailedRecipe(
        @Header("Access-Token") accessToken: String,
        @Path("recipeId") recipeId: Long
    ): BaseResponse<DetailedRecipe>

    // NOTE: 레시피 좋아요 <-> 좋아요 취소
    @POST("/recipes/{recipeId}/like")
    suspend fun postRecipeLike(
        @Header("Access-Token") accessToken: String,
        @Path("recipeId") recipeId: Long
    ): BaseResponse<PostRecipeLikeRes>

    // NOTE: 내가 쓴 리뷰
    @GET("/my-reviews")
    suspend fun getMyReviews(
        @Header("Access-Token") accessToken: String,
        @Query("page") pageNum: Int
    ): BaseResponse<List<Review>>
}

