package com.mju.csmoa.util.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import com.mju.csmoa.UserInfo
import com.mju.csmoa.login.domain.model.GetJwtTokenRes
import com.mju.csmoa.login.domain.model.JwtToken
import com.mju.csmoa.login.domain.model.PostLoginRes
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.util.Constants
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class UserInfoProtoManager(private val context: Context) {

    private val Context.myDataStore by dataStore(
        fileName = "user_info.proto",
        serializer = UserInfoSerializer
    )

    suspend fun updateUserInfo(jwtToken: JwtToken) {
        context.myDataStore.updateData { userInfo ->
            userInfo.toBuilder()
                .setUserId(jwtToken.userId)
                .setAccessToken(jwtToken.accessToken)
                .setRefreshToken(jwtToken.refreshToken)
                .build()
        }
    }

    suspend fun getUserInfo(): UserInfo {
        return context.myDataStore.data.catch { exception ->
            if (exception is IOException) {
                Log.e(Constants.TAG, "Error reading sort order preferences.", exception)
                emit(UserInfo.getDefaultInstance())
            } else {
                throw exception
            }
        }.first()
    }

//    fun refreshJwtToken(refreshToken: String): String {
//        RetrofitManager.instance.refreshJwtToken(
//            refreshToken,
//            completion = { statusCode: Int, getJwtTokenRes: GetJwtTokenRes? ->
//                when (statusCode) {
//                    100 -> {
//
//                    }
//                    201 -> {
//
//                    }
//                    202 -> {
//
//                    }
//                    else -> {
//
//                    }
//                }
//            }
//        )
//    }

}