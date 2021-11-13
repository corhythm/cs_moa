package com.mju.csmoa.retrofit

import android.util.Log
import com.mju.csmoa.util.Constants.TAG
import com.mju.csmoa.util.isJsonArray
import com.mju.csmoa.util.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofitClient: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {

        // okhttp instance 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 추가 (전반적인 통신 내역 볼 수 있음)
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "RetrofitClient -getClient() called / $message")

            when {
                message.isJsonObject() ->
                    Log.d(TAG, JSONObject(message).toString(4))
                message.isJsonArray() ->
                    Log.d(TAG, JSONObject(message).toString(4))
                else ->
                    try {
                        Log.d(TAG, JSONObject(message).toString(4))
                    } catch (e: Exception) {
                        Log.d(TAG, "exception / $message")
                    }
            }
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가
        client.addInterceptor(loggingInterceptor)


        // 기본 파라미터 인터셉터 설정
//        val baseParameterInterceptor = Interceptor { chain ->
//            Log.d(TAG, "RetrofitClient -intercept() called")
//            // original request
//            val originalRequest = chain.request()
//
//            // query parameter 추가하기
//            // localhost:4000?client_id=100
//            val addedUrl =
//                originalRequest.url.newBuilder().addQueryParameter("client_id", "100").build()
//
//            val finalRequest = originalRequest.newBuilder()
//                .url(addedUrl)
//                .method(originalRequest.method, originalRequest.body)
//                .build()
//
//            return@Interceptor chain.proceed(finalRequest)
//        }

        // 위에서 설정한 기본 파라미터 인터셉터를 okhttp 클라이언트에 추가
//        client.addInterceptor(baseParameterInterceptor)

        // connection timeout
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true) // 실패할 경우 다시 시도할 건지

        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }

        return retrofitClient
    }
}
