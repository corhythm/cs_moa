package com.mju.csmoa.retrofit

import android.util.Log
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.isJsonArray
import com.mju.csmoa.common.util.isJsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofitClient: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        // okhttp instance 생성
        val client = OkHttpClient.Builder()

        // 통신 중 일어나는 로그를 인터셉트하는 Interceptor
        // 로그를 찍기 위해 로깅 인터셉터 추가 (전반적인 통신 내역 볼 수 있음)
        val loggingInterceptor = HttpLoggingInterceptor { message ->
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
