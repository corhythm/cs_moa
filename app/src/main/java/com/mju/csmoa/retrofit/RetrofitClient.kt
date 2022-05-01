package com.mju.csmoa.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.isJsonArray
import com.mju.csmoa.common.util.isJsonObject
import com.mju.csmoa.login.SignInActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import www.sanju.motiontoast.MotionToastStyle
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var retrofitClient: Retrofit? = null

    fun getRetrofitClient(baseUrl: String): Retrofit? {
        // okhttp instance 생성
        val okHttpClient = OkHttpClient.Builder()

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
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.HEADERS)
        }


        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가
        okHttpClient.addInterceptor(loggingInterceptor)
        // connection timeout
        okHttpClient.connectTimeout(10, TimeUnit.SECONDS)
        okHttpClient.readTimeout(10, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(10, TimeUnit.SECONDS)
        okHttpClient.retryOnConnectionFailure(true) // 실패할 경우 다시 시도할 건지

        if (retrofitClient == null) {
            try {
                retrofitClient = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient.build())
                    .build()
            } catch (ex: IllegalArgumentException) { // 주소:port가 올바르지 않을 때
                Log.e(TAG, "getRetrofitClient: ${ex.message}")
            }
        }
        return retrofitClient
    }

    // signInActivity에서 dialog를 통해 baseUrl 변경 시 사용
    fun updateRetrofitClient(baseUrl: String, context: Context): Boolean {
        retrofitClient = null
        retrofitClient = getRetrofitClient(baseUrl)

        if (retrofitClient == null && context is SignInActivity) {
            MyApplication.makeToast(
                activity = context,
                title = "Invalid server address",
                content = "적절하지 않은 서버 주소입니다.",
                motionToastStyle = MotionToastStyle.ERROR
            )
        }
        return retrofitClient != null
    }
}
