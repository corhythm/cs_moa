package com.mju.csmoa.retrofit

import android.util.Log
import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.model.PostOAuthLogin
import com.mju.csmoa.login.domain.model.PostSignUpReq
import com.mju.csmoa.util.Constants.API_BASE_URL
import com.mju.csmoa.util.Constants.TAG
import retrofit2.Call
import retrofit2.Response

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

                when (response.code()) {
                    200 -> { // 데이터 수신에 성공했을 때, 이건 따로 정의한 statusCode가 아닌 httpCode임
                        response.body()?.let {
                            val body = it.asJsonObject
                            Log.d(TAG, "RetrofitManager -onResponse() called / body = $body")
                            val result = body.getAsJsonObject("result")
                            Log.d(TAG, "RetrofitManager -onResponse() called / results = $result")
                            val statusCode = body.get("code").asInt
                            completion(statusCode)
                        }
                    }
                    else -> Log.d(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                // 이건 아예 false이고 true인 경우에도 valitaion 처리를 해줘야 할 경우가 있다.
                Log.d(TAG, "RetrofitManager -onFailure() called")
            }
        })
    }

    // 카카오 로그인
    fun oauthLogin(postOAuthLogin: PostOAuthLogin, completion: (Int) -> Unit) {
        val oauthLoginCallback = iRetrofit?.kakaoLogin(postOAuthLogin) ?: return

        oauthLoginCallback.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> { // 데이터 수신에 성공했을 때, 이건 따로 정의한 statusCode가 아닌 httpCode임
                        response.body()?.let {
                            val body = it.asJsonObject
                            Log.d(TAG, "RetrofitManager -onResponse() called / body = $body")
                            val result = body.getAsJsonObject("result")
                            Log.d(TAG, "RetrofitManager -onResponse() called / results = $result")
                            val statusCode = body.get("code").asInt
                            completion(statusCode)
                        }
                    }
                    else -> Log.d(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "oauthLogin -onFailure() called")
            }
        })
    }

}