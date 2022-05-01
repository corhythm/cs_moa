package com.mju.csmoa.common.util.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import com.mju.csmoa.JwtTokenInfo
import com.mju.csmoa.login.domain.JwtToken
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import java.lang.Exception

class JwtTokenInfoProtoManager(private val context: Context) {

    private val Context.myDataStore by dataStore(
        fileName = "jwt_token_info.proto",
        serializer = JwtTokenInfoSerializer
    )

    suspend fun updateJwtTokenInfo(jwtToken: JwtToken) {
        context.myDataStore.updateData { jwtTokenInfo ->
            jwtTokenInfo.toBuilder()
                .setUserId(jwtToken.userId)
                .setAccessToken(jwtToken.accessToken)
                .setRefreshToken(jwtToken.refreshToken)
                .build()
        }
    }

    suspend fun getJwtTokenInfo(): JwtTokenInfo? {
        val jwtTokenInfo = context.myDataStore.data.catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(JwtTokenInfo.getDefaultInstance())
            } else {
                throw exception
            }
        }.firstOrNull() ?: return null
        Log.d(
            TAG,
            "JwtTokenInfoProtoManager -getJwtTokenInfo() called / jwtTokenInfo = $jwtTokenInfo"
        )

        // 근데 이 로직이 필요한가?
        // accessToken 값이 아예 없을 때 -> 제일 처음에 로그인 하는 사용자일 경우
        if (jwtTokenInfo.accessToken.isEmpty() || jwtTokenInfo.accessToken == null)
            return null

        // accessToken 만료됐으면 갱신
        if (MyApplication.instance.jwtService.isAccessTokenExpired(jwtTokenInfo.accessToken)) {
            return try { // socket error 터질 수도 있음
                val jwtToken =
                    RetrofitManager.instance.getRefreshJwtToken(jwtTokenInfo.refreshToken!!)
                updateJwtTokenInfo(jwtToken!!)

                jwtTokenInfo.toBuilder()
                    .setUserId(jwtToken.userId)
                    ?.setAccessToken(jwtToken.accessToken)
                    ?.setRefreshToken(jwtToken.refreshToken)
                    ?.build()
            } catch (ex: Exception) {
                Log.d(TAG, "JwtTokenInfoProtoManager -getJwtTokenInfo() called / ${ex.message}")
                null
            }
        } else {
            return jwtTokenInfo
        }
    }
}