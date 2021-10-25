package com.mju.csmoa.retrofit

import android.util.Log
import com.google.gson.JsonElement
import com.mju.csmoa.login.domain.serializable.PostSignUpReq
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
    fun signUp(email: String, password: String, nickname: String, completion: (Int) -> Unit) {
        val callSignUp =
            iRetrofit?.signUp(PostSignUpReq(email = email, password = password, nickname = nickname)) ?: return

        callSignUp.enqueue(object : retrofit2.Callback<JsonElement> {
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
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                // 이건 아예 false이고 true인 경우에도 valitaion 처리를 해줘야 할 경우가 있다.

            }
        })
    }

}