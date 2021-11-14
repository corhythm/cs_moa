package com.mju.csmoa.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.mju.csmoa.R
import com.mju.csmoa.util.datastore.JwtTokenInfoProtoManager
import com.mju.csmoa.util.room.database.LocalRoomDatabase
import com.mju.csmoa.util.room.repository.SearchHistoryRepository
import com.mju.csmoa.util.secret.JwtService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LocalRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SearchHistoryRepository(database.searchHistoryDao()) }
    val jwtTokenInfoProtoManager by lazy { JwtTokenInfoProtoManager(this) }
    val jwtService by lazy { JwtService() }

    companion object {
        lateinit var instance: MyApplication
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 카카오 로그인 활성화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
//        getAppKeyHash()
//        Log.d(TAG, " -onCreate() called / (kakao) keyHash = ${Utility.getKeyHash(this)}")
    }



//    @RequiresApi(Build.VERSION_CODES.P)
//    private fun getAppKeyHash() {
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            val signatures = info.signingInfo.apkContentsSigners
//            val md = MessageDigest.getInstance("SHA")
//            for (signature in signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val key = String(Base64.encode(md.digest(), 0))
//                Log.d(TAG, "(fun) hash key = $key")
//            }
//        } catch(e: Exception) {
//            Log.e("name not found", e.toString())
//        }
//    }
}