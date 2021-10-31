package com.mju.csmoa.retrofit

import android.util.Log
import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.model.PostLoginReq
import com.mju.csmoa.login.domain.model.PostLoginRes
import com.mju.csmoa.login.domain.model.PostOAuthLoginReq
import com.mju.csmoa.login.domain.model.PostSignUpReq
import com.mju.csmoa.util.Constants.API_BASE_URL
import com.mju.csmoa.util.Constants.TAG
import retrofit2.Call
import retrofit2.Response
import java.lang.NullPointerException

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http call 만들기
    // get IRetrofit interface
    private val iRetrofit: IRetrofit? =
        RetrofitClient.getClient(API_BASE_URL)?.create(IRetrofit::class.java)

    // 회원가입 api 호출
    fun signUp(postSignUpReq: PostSignUpReq, completion: (Int) -> Unit) {
        val signUpCallback =
            iRetrofit?.signUp(postSignUpReq) ?: return

        signUpCallback.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                try {
                    when (response.code()) {
                        200 -> { // 데이터 수신에 성공했을 때, 이건 따로 정의한 statusCode가 아닌 httpCode임
                            response.body()?.let {
                                val body = it.asJsonObject
                                Log.d(TAG, "RetrofitManager -onResponse() called / body = $body")
                                val result = body.getAsJsonObject("result")
                                Log.d(
                                    TAG,
                                    "RetrofitManager -onResponse() called / results = $result"
                                )
                                val statusCode = body.get("code").asInt
                                completion(statusCode)
                            }
                        }
                        else -> Log.d(TAG, "Error: ${response.code()}")
                    }
                } catch (ex: NullPointerException) { // serialize 실패하면
                    completion(500)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                // 이건 아예 false이고 true인 경우에도 valitaion 처리를 해줘야 할 경우가 있다.
                Log.d(TAG, "RetrofitManager -onFailure() / signUp called")
            }
        })
    }

    fun login(postLoginReq: PostLoginReq, completion: (Int, PostLoginRes?) -> Unit) {
        val loginCallback = iRetrofit?.login(postLoginReq) ?: return

        loginCallback.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            try {
                                val body = it.asJsonObject
                                Log.d(TAG, "RetrofitManager -onResponse() called / body = $body")
                                val result = body.getAsJsonObject("result")
                                val statusCode = body.get("code").asInt

                                // 콜백 함수 전달
                                completion(
                                    statusCode, PostLoginRes(
                                        userId = result.getAsJsonPrimitive("userId").asLong,
                                        accessToken = result.getAsJsonPrimitive("accessToken").asString,
                                        refreshToken = result.getAsJsonPrimitive("refreshToken").asString
                                    )
                                )
                            } catch (ex: NullPointerException) { // serialize 실패하면
                                completion(500, null)
                            }
                        }
                    }
                    else -> Log.d(TAG, "Error: login / ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager -onFailure() / login called")
            }
        })
    }

    // OAuth 로그인
    fun oAuthLogin(postOAuthLoginReq: PostOAuthLoginReq, completion: (Int, PostLoginRes?) -> Unit) {
        val oauthLoginCallback = iRetrofit?.oAuthLogin(postOAuthLoginReq) ?: return

        oauthLoginCallback.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> { // 데이터 수신에 성공했을 때, 이건 따로 정의한 statusCode가 아닌 httpCode임
                        response.body()?.let {

                            try {
                                val body = it.asJsonObject
                                Log.d(TAG, "RetrofitManager -onResponse() called / body = $body")
                                val result = body.getAsJsonObject("result")
                                val statusCode = body.get("code").asInt

                                // 콜백 함수 전달
                                completion(
                                    statusCode, PostLoginRes(
                                        userId = result.getAsJsonPrimitive("userId").asLong,
                                        accessToken = result.getAsJsonPrimitive("accessToken").asString,
                                        refreshToken = result.getAsJsonPrimitive("refreshToken").asString
                                    )
                                )
                            } catch (ex: NullPointerException) { // serialize 실패하면
                                val statusCode = it.asJsonObject?.get("code")?.asInt
                                completion(statusCode ?: 500, null)
                            }

                        }
                    }
                    else -> Log.d(TAG, "Error: oAuthLogin / ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "oauthLogin -onFailure() called")
            }
        })
    }

}